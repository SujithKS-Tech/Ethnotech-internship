package com.chatapp.api.dto;

import com.chatapp.domain.model.ChatType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

public record CreateChatRequest(
    String name,
    @NotNull ChatType type,
    @NotEmpty Set<UUID> participantIds
) {
}
