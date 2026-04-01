package com.chatapp.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record WebSocketChatRequest(
    @NotNull UUID chatRoomId,
    @NotBlank String content
) {
}
