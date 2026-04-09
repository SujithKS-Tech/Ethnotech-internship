package com.chatapp.api.dto;

import java.util.UUID;

public record UserSummaryResponse(
    UUID id,
    String username,
    String displayName,
    boolean online
) {
}
