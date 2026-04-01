package com.chatapp.service;

import com.chatapp.api.dto.ChatParticipantResponse;
import com.chatapp.api.dto.ChatRoomResponse;
import com.chatapp.api.dto.CreateChatRequest;
import com.chatapp.api.dto.MessageReadReceiptResponse;
import com.chatapp.api.dto.MessageReactionResponse;
import com.chatapp.api.dto.MessageResponse;
import com.chatapp.domain.model.ChatParticipant;
import com.chatapp.domain.model.ChatRoom;
import com.chatapp.domain.model.ChatType;
import com.chatapp.domain.model.Message;
import com.chatapp.domain.model.MessageReaction;
import com.chatapp.domain.model.MessageReadReceipt;
import com.chatapp.domain.model.MessageStatus;
import com.chatapp.domain.model.User;
import com.chatapp.domain.repository.ChatParticipantRepository;
import com.chatapp.domain.repository.ChatRoomRepository;
import com.chatapp.domain.repository.MessageReactionRepository;
import com.chatapp.domain.repository.MessageReadReceiptRepository;
import com.chatapp.domain.repository.MessageRepository;
import com.chatapp.domain.repository.UserRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final MessageRepository messageRepository;
    private final MessageReadReceiptRepository messageReadReceiptRepository;
    private final MessageReactionRepository messageReactionRepository;
    private final UserRepository userRepository;
    private final MessageCacheService messageCacheService;
    private final NotificationService notificationService;
    private final FileStorageService fileStorageService;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public ChatRoomResponse createChat(CreateChatRequest request, User currentUser) {
        Set<UUID> participantIds = new LinkedHashSet<>(request.participantIds());
        participantIds.add(currentUser.getId());

        if (request.type() == ChatType.PRIVATE) {
            if (participantIds.size() != 2) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Private chat must contain exactly two users");
            }
            UUID otherUserId = participantIds.stream()
                .filter(id -> !id.equals(currentUser.getId()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Private chat requires another participant"));

            return chatRoomRepository.findPrivateChatBetweenUsers(currentUser.getId(), otherUserId)
                .or(() -> chatRoomRepository.findPrivateChatBetweenUsers(otherUserId, currentUser.getId()))
                .map(chat -> toChatRoomResponse(chat, currentUser.getId()))
                .orElseGet(() -> createAndPersistChat(request, currentUser, participantIds));
        }

        if (request.name() == null || request.name().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Group chat name is required");
        }
        return createAndPersistChat(request, currentUser, participantIds);
    }

    @Transactional
    public List<ChatRoomResponse> getChatsForUser(User currentUser) {
        List<ChatRoom> chats = chatRoomRepository.findAllForUser(currentUser.getId());
        markChatsAsDelivered(chats, currentUser);
        return chats.stream()
            .map(chatRoom -> toChatRoomResponse(chatRoom, currentUser.getId()))
            .toList();
    }

    @Transactional
    public List<MessageResponse> getMessageHistory(UUID chatRoomId, User currentUser) {
        validateMembership(chatRoomId, currentUser.getId());
        markChatAsRead(chatRoomId, currentUser);

        List<MessageResponse> messages = messageRepository.findByChatRoomIdOrderByCreatedAtAsc(chatRoomId).stream()
            .map(this::toMessageResponse)
            .toList();
        messageCacheService.overwriteHistory(chatRoomId, messages);
        return messages;
    }

    @Transactional
    public MessageResponse sendMessage(UUID chatRoomId, String content, User sender) {
        return createMessage(chatRoomId, content, null, sender, false);
    }

    @Transactional
    public MessageResponse sendAttachment(UUID chatRoomId, String content, MultipartFile file, User sender) {
        FileStorageService.StoredFile storedFile = fileStorageService.store(file);
        boolean voiceNote = storedFile.contentType() != null && storedFile.contentType().startsWith("audio/");
        return createMessage(chatRoomId, content, storedFile, sender, voiceNote);
    }

    @Transactional
    public MessageResponse editMessage(UUID messageId, String content, User currentUser) {
        Message message = getOwnedMessage(messageId, currentUser.getId());
        if (message.isDeleted()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Deleted messages cannot be edited");
        }
        message.setContent(content.trim());
        message.setEdited(true);
        message.setEditedAt(Instant.now());
        Message saved = messageRepository.save(message);
        MessageResponse response = toMessageResponse(saved);
        publishMessageUpdate(response);
        return response;
    }

    @Transactional
    public MessageResponse deleteMessage(UUID messageId, User currentUser) {
        Message message = getOwnedMessage(messageId, currentUser.getId());
        message.setDeleted(true);
        message.setDeletedAt(Instant.now());
        message.setEdited(false);
        message.setEditedAt(null);
        message.setContent("This message was deleted");
        Message saved = messageRepository.save(message);
        MessageResponse response = toMessageResponse(saved);
        publishMessageUpdate(response);
        return response;
    }

    @Transactional
    public MessageResponse toggleReaction(UUID messageId, String emoji, User currentUser) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));
        validateMembership(message.getChatRoom().getId(), currentUser.getId());

        messageReactionRepository.findByMessageIdAndUserIdAndEmoji(messageId, currentUser.getId(), emoji)
            .ifPresentOrElse(
                messageReactionRepository::delete,
                () -> {
                    MessageReaction reaction = new MessageReaction();
                    reaction.setMessage(message);
                    reaction.setUser(currentUser);
                    reaction.setEmoji(emoji);
                    messageReactionRepository.save(reaction);
                }
            );

        MessageResponse response = toMessageResponse(messageRepository.findById(messageId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found")));
        publishMessageUpdate(response);
        return response;
    }

    @Transactional
    public void markChatAsRead(UUID chatRoomId, User currentUser) {
        validateMembership(chatRoomId, currentUser.getId());

        List<Message> messages = messageRepository.findByChatRoomIdOrderByCreatedAtAsc(chatRoomId);
        boolean updated = false;
        for (Message message : messages) {
            if (message.getSender().getId().equals(currentUser.getId()) || message.isDeleted()) {
                continue;
            }

            MessageReadReceipt receipt = messageReadReceiptRepository.findByMessageIdAndUserId(message.getId(), currentUser.getId())
                .orElseGet(() -> {
                    MessageReadReceipt newReceipt = new MessageReadReceipt();
                    newReceipt.setMessage(message);
                    newReceipt.setUser(currentUser);
                    return newReceipt;
                });

            if (receipt.getReadAt() == null) {
                receipt.setReadAt(Instant.now());
                messageReadReceiptRepository.save(receipt);
                message.setMessageStatus(MessageStatus.READ);
                messageRepository.save(message);
                updated = true;
            }
        }

        if (updated) {
            List<MessageResponse> refreshed = messageRepository.findByChatRoomIdOrderByCreatedAtAsc(chatRoomId).stream()
                .map(this::toMessageResponse)
                .toList();
            messageCacheService.overwriteHistory(chatRoomId, refreshed);
            messagingTemplate.convertAndSend("/topic/chat.receipts." + chatRoomId, refreshed);
        }
    }

    public void assertMembership(UUID chatRoomId, UUID userId) {
        validateMembership(chatRoomId, userId);
    }

    private MessageResponse createMessage(UUID chatRoomId, String content, FileStorageService.StoredFile storedFile, User sender, boolean voiceNote) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat room not found"));
        validateMembership(chatRoomId, sender.getId());

        String trimmedContent = content == null ? null : content.trim();
        if ((trimmedContent == null || trimmedContent.isBlank()) && storedFile == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message content or attachment is required");
        }

        Message message = new Message();
        message.setChatRoom(chatRoom);
        message.setSender(sender);
        message.setContent(trimmedContent == null || trimmedContent.isBlank() ? null : trimmedContent);
        message.setAttachmentName(storedFile == null ? null : storedFile.originalName());
        message.setAttachmentType(storedFile == null ? null : storedFile.contentType());
        message.setAttachmentUrl(storedFile == null ? null : storedFile.publicUrl());
        message.setVoiceNote(voiceNote);
        message.setMessageStatus(MessageStatus.SENT);
        Message savedMessage = messageRepository.save(message);

        MessageResponse response = toMessageResponse(savedMessage);
        messageCacheService.cacheMessage(response);

        chatParticipantRepository.findByChatRoomIdAndUserIdNot(chatRoomId, sender.getId())
            .forEach(participant -> notificationService.createMessageNotification(participant.getUser(), savedMessage));

        return response;
    }

    private void markChatsAsDelivered(List<ChatRoom> chats, User currentUser) {
        for (ChatRoom chat : chats) {
            boolean updated = false;
            List<Message> messages = messageRepository.findByChatRoomIdOrderByCreatedAtAsc(chat.getId());
            for (Message message : messages) {
                if (message.getSender().getId().equals(currentUser.getId()) || message.isDeleted()) {
                    continue;
                }
                if (message.getMessageStatus() == MessageStatus.SENT) {
                    message.setMessageStatus(MessageStatus.DELIVERED);
                    messageRepository.save(message);
                    updated = true;
                }
            }

            if (updated) {
                List<MessageResponse> history = messages.stream()
                    .map(this::toMessageResponse)
                    .toList();
                messageCacheService.overwriteHistory(chat.getId(), history);
                messagingTemplate.convertAndSend("/topic/chat.refresh." + chat.getId(), history);
            }
        }
    }

    private void publishMessageUpdate(MessageResponse response) {
        List<MessageResponse> history = messageRepository.findByChatRoomIdOrderByCreatedAtAsc(response.chatRoomId()).stream()
            .map(this::toMessageResponse)
            .toList();
        messageCacheService.overwriteHistory(response.chatRoomId(), history);
        messagingTemplate.convertAndSend("/topic/chat." + response.chatRoomId(), response);
        messagingTemplate.convertAndSend("/topic/chat.refresh." + response.chatRoomId(), history);
    }

    private Message getOwnedMessage(UUID messageId, UUID userId) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));
        validateMembership(message.getChatRoom().getId(), userId);
        if (!message.getSender().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only modify your own messages");
        }
        return message;
    }

    private ChatRoomResponse createAndPersistChat(CreateChatRequest request, User currentUser, Set<UUID> participantIds) {
        List<User> participants = userRepository.findAllById(participantIds);
        if (participants.size() != participantIds.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "One or more participants do not exist");
        }

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(request.type() == ChatType.PRIVATE ? null : request.name().trim());
        chatRoom.setChatType(request.type());
        chatRoom.setCreatedBy(currentUser);

        List<ChatParticipant> chatParticipants = new ArrayList<>();
        for (User participant : participants) {
            ChatParticipant chatParticipant = new ChatParticipant();
            chatParticipant.setChatRoom(chatRoom);
            chatParticipant.setUser(participant);
            chatParticipant.setAdmin(participant.getId().equals(currentUser.getId()));
            chatParticipants.add(chatParticipant);
        }
        chatRoom.setParticipants(chatParticipants);

        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        return toChatRoomResponse(savedChatRoom, currentUser.getId());
    }

    private void validateMembership(UUID chatRoomId, UUID userId) {
        if (!chatParticipantRepository.existsByChatRoomIdAndUserId(chatRoomId, userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not a participant in this chat room");
        }
    }

    private ChatRoomResponse toChatRoomResponse(ChatRoom chatRoom, UUID currentUserId) {
        List<ChatParticipant> participants = chatParticipantRepository.findByChatRoomId(chatRoom.getId());
        List<ChatParticipantResponse> participantResponses = participants.stream()
            .map(participant -> new ChatParticipantResponse(
                participant.getUser().getId(),
                participant.getUser().getUsername(),
                participant.getUser().getDisplayName(),
                participant.getUser().isOnline(),
                participant.isAdmin()
            ))
            .toList();

        MessageResponse lastMessage = messageRepository.findFirstByChatRoomIdOrderByCreatedAtDesc(chatRoom.getId())
            .map(this::toMessageResponse)
            .orElse(null);

        String chatName = chatRoom.getChatType() == ChatType.GROUP
            ? chatRoom.getName()
            : participantResponses.stream()
                .filter(participant -> !participant.userId().equals(currentUserId))
                .map(ChatParticipantResponse::displayName)
                .findFirst()
                .orElse("Private Chat");

        long unreadCount = messageRepository.findByChatRoomIdOrderByCreatedAtAsc(chatRoom.getId()).stream()
            .filter(message -> !message.getSender().getId().equals(currentUserId))
            .filter(message -> !message.isDeleted())
            .filter(message -> messageReadReceiptRepository.findByMessageIdAndUserId(message.getId(), currentUserId)
                .map(receipt -> receipt.getReadAt() == null)
                .orElse(true))
            .count();

        return new ChatRoomResponse(
            chatRoom.getId(),
            chatName,
            chatRoom.getChatType(),
            participantResponses,
            lastMessage,
            chatRoom.getUpdatedAt(),
            unreadCount
        );
    }

    private MessageResponse toMessageResponse(Message message) {
        List<MessageReadReceiptResponse> receipts = messageReadReceiptRepository.findByMessageId(message.getId()).stream()
            .filter(receipt -> receipt.getReadAt() != null)
            .map(receipt -> new MessageReadReceiptResponse(
                receipt.getUser().getId(),
                receipt.getUser().getDisplayName(),
                receipt.getReadAt()
            ))
            .toList();

        List<MessageReactionResponse> reactions = messageReactionRepository.findByMessageId(message.getId()).stream()
            .map(reaction -> new MessageReactionResponse(
                reaction.getUser().getId(),
                reaction.getUser().getDisplayName(),
                reaction.getEmoji()
            ))
            .toList();

        return new MessageResponse(
            message.getId(),
            message.getChatRoom().getId(),
            message.getSender().getId(),
            message.getSender().getDisplayName(),
            message.getContent(),
            message.getAttachmentName(),
            message.getAttachmentType(),
            message.getAttachmentUrl(),
            message.isVoiceNote(),
            message.getMessageStatus(),
            message.getCreatedAt(),
            message.isEdited(),
            message.getEditedAt(),
            message.isDeleted(),
            message.getDeletedAt(),
            receipts,
            reactions
        );
    }
}
