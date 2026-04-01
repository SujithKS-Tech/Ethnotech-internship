package com.chatapp.api.dto;

import com.chatapp.domain.model.ChatType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChatRoomResponse(
    UUID id,
    String name,
    ChatType type,
    List<ChatParticipantResponse> participants,
    MessageResponse lastMessage,
    Instant updatedAt,
    long unreadCount
) {
}
