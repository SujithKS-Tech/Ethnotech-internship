package com.chatapp.api.dto;

import com.chatapp.domain.model.MessageStatus;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageResponse(
    UUID id,
    UUID chatRoomId,
    UUID senderId,
    String senderName,
    String content,
    String attachmentName,
    String attachmentType,
    String attachmentUrl,
    boolean voiceNote,
    MessageStatus status,
    Instant createdAt,
    boolean edited,
    Instant editedAt,
    boolean deleted,
    Instant deletedAt,
    List<MessageReadReceiptResponse> readReceipts,
    List<MessageReactionResponse> reactions
) implements Serializable {
}
