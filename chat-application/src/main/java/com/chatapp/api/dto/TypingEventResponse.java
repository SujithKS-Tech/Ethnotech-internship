package com.chatapp.api.dto;

import java.io.Serializable;
import java.util.UUID;

public record TypingEventResponse(
    UUID chatRoomId,
    UUID userId,
    String displayName,
    boolean typing
) implements Serializable {
}
