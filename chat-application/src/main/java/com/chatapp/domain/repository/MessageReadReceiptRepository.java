package com.chatapp.domain.repository;

import com.chatapp.domain.model.MessageReadReceipt;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageReadReceiptRepository extends JpaRepository<MessageReadReceipt, UUID> {

    List<MessageReadReceipt> findByMessageId(UUID messageId);

    List<MessageReadReceipt> findByMessageChatRoomIdAndUserId(UUID chatRoomId, UUID userId);

    Optional<MessageReadReceipt> findByMessageIdAndUserId(UUID messageId, UUID userId);
}
