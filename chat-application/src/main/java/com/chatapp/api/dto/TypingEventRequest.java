package com.chatapp.api.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record TypingEventRequest(
    @NotNull UUID chatRoomId,
    boolean typing
) {
}
