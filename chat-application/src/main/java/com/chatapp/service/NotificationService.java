package com.chatapp.service;

import com.chatapp.api.dto.NotificationResponse;
import com.chatapp.domain.model.Message;
import com.chatapp.domain.model.Notification;
import com.chatapp.domain.model.NotificationType;
import com.chatapp.domain.model.User;
import com.chatapp.domain.repository.NotificationRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public NotificationResponse createMessageNotification(User recipient, Message message) {
        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setTitle("New message from " + message.getSender().getDisplayName());
        notification.setContent(message.getContent() != null && !message.getContent().isBlank()
            ? message.getContent()
            : "Shared attachment: " + message.getAttachmentName());
        notification.setNotificationType(NotificationType.MESSAGE);

        Notification savedNotification = notificationRepository.save(notification);
        NotificationResponse response = toResponse(savedNotification);
        messagingTemplate.convertAndSendToUser(recipient.getEmail(), "/queue/notifications", response);
        return response;
    }

    public List<NotificationResponse> getNotifications(UUID userId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId).stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional
    public NotificationResponse markAsRead(UUID notificationId, UUID userId) {
        Notification notification = notificationRepository.findByIdAndRecipientId(notificationId, userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
        notification.setRead(true);
        return toResponse(notificationRepository.save(notification));
    }

    private NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
            notification.getId(),
            notification.getTitle(),
            notification.getContent(),
            notification.getNotificationType(),
            notification.isRead(),
            notification.getCreatedAt()
        );
    }
}
