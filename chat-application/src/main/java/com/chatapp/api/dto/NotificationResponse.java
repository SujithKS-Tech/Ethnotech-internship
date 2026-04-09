package com.chatapp.api.dto;

import com.chatapp.domain.model.NotificationType;
import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
    UUID id,
    String title,
    String content,
    NotificationType type,
    boolean read,
    Instant createdAt
) {
}
