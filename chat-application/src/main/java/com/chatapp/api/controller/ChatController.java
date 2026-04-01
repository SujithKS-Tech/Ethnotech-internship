package com.chatapp.api.controller;

import com.chatapp.api.dto.ChatRoomResponse;
import com.chatapp.api.dto.CreateChatRequest;
import com.chatapp.api.dto.MessageRequest;
import com.chatapp.api.dto.MessageResponse;
import com.chatapp.api.dto.ReactionRequest;
import com.chatapp.api.dto.UpdateMessageRequest;
import com.chatapp.security.AppUserPrincipal;
import com.chatapp.service.ChatService;
import com.chatapp.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping
    public ChatRoomResponse createChat(@Valid @RequestBody CreateChatRequest request,
                                       @AuthenticationPrincipal AppUserPrincipal principal) {
        return chatService.createChat(request, userService.getByEmail(principal.getUsername()));
    }

    @GetMapping
    public List<ChatRoomResponse> getChats(@AuthenticationPrincipal AppUserPrincipal principal) {
        return chatService.getChatsForUser(userService.getByEmail(principal.getUsername()));
    }

    @GetMapping("/{chatRoomId}/messages")
    public List<MessageResponse> getMessageHistory(@PathVariable UUID chatRoomId,
                                                   @AuthenticationPrincipal AppUserPrincipal principal) {
        return chatService.getMessageHistory(chatRoomId, userService.getByEmail(principal.getUsername()));
    }

    @PostMapping("/{chatRoomId}/messages")
    public MessageResponse sendMessage(@PathVariable UUID chatRoomId,
                                       @Valid @RequestBody MessageRequest request,
                                       @AuthenticationPrincipal AppUserPrincipal principal) {
        MessageResponse response = chatService.sendMessage(chatRoomId, request.content(), userService.getByEmail(principal.getUsername()));
        messagingTemplate.convertAndSend("/topic/chat." + chatRoomId, response);
        return response;
    }

    @PostMapping(path = "/{chatRoomId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MessageResponse sendAttachment(@PathVariable UUID chatRoomId,
                                          @RequestParam(value = "content", required = false) String content,
                                          @RequestParam("file") MultipartFile file,
                                          @AuthenticationPrincipal AppUserPrincipal principal) {
        MessageResponse response = chatService.sendAttachment(chatRoomId, content, file, userService.getByEmail(principal.getUsername()));
        messagingTemplate.convertAndSend("/topic/chat." + chatRoomId, response);
        return response;
    }

    @PostMapping(path = "/{chatRoomId}/voice-notes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public MessageResponse sendVoiceNote(@PathVariable UUID chatRoomId,
                                         @RequestParam("file") MultipartFile file,
                                         @AuthenticationPrincipal AppUserPrincipal principal) {
        MessageResponse response = chatService.sendAttachment(chatRoomId, "Voice note", file, userService.getByEmail(principal.getUsername()));
        messagingTemplate.convertAndSend("/topic/chat." + chatRoomId, response);
        return response;
    }

    @PatchMapping("/{chatRoomId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable UUID chatRoomId,
                                           @AuthenticationPrincipal AppUserPrincipal principal) {
        chatService.markChatAsRead(chatRoomId, userService.getByEmail(principal.getUsername()));
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/messages/{messageId}")
    public MessageResponse editMessage(@PathVariable UUID messageId,
                                       @Valid @RequestBody UpdateMessageRequest request,
                                       @AuthenticationPrincipal AppUserPrincipal principal) {
        return chatService.editMessage(messageId, request.content(), userService.getByEmail(principal.getUsername()));
    }

    @DeleteMapping("/messages/{messageId}")
    public MessageResponse deleteMessage(@PathVariable UUID messageId,
                                         @AuthenticationPrincipal AppUserPrincipal principal) {
        return chatService.deleteMessage(messageId, userService.getByEmail(principal.getUsername()));
    }

    @PostMapping("/messages/{messageId}/reactions")
    public MessageResponse toggleReaction(@PathVariable UUID messageId,
                                          @Valid @RequestBody ReactionRequest request,
                                          @AuthenticationPrincipal AppUserPrincipal principal) {
        return chatService.toggleReaction(messageId, request.emoji(), userService.getByEmail(principal.getUsername()));
    }
}
