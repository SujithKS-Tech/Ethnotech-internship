package com.chatapp.api.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public record MessageReadReceiptResponse(
    UUID userId,
    String displayName,
    Instant readAt
) implements Serializable {
}
