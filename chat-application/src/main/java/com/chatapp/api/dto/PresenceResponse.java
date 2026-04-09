package com.chatapp.api.dto;

import java.time.Instant;
import java.util.UUID;

public record PresenceResponse(
    UUID userId,
    String username,
    boolean online,
    Instant lastSeen
) {
}
