package com.chatapp.api.dto;

import java.io.Serializable;
import java.util.UUID;

public record MessageReactionResponse(
    UUID userId,
    String displayName,
    String emoji
) implements Serializable {
}
