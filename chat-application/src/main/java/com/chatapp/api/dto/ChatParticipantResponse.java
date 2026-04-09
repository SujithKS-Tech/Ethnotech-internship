package com.chatapp.api.dto;

import java.util.UUID;

public record ChatParticipantResponse(
    UUID userId,
    String username,
    String displayName,
    boolean online,
    boolean admin
) {
}
