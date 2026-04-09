package com.chatapp.domain.repository;

import com.chatapp.domain.model.Message;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    List<Message> findByChatRoomIdOrderByCreatedAtAsc(UUID chatRoomId);

    Optional<Message> findFirstByChatRoomIdOrderByCreatedAtDesc(UUID chatRoomId);
}
