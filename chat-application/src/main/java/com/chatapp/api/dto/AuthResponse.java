package com.chatapp.api.dto;

import java.util.UUID;

public record AuthResponse(
    String token,
    UUID userId,
    String username,
    String displayName
) {
}
