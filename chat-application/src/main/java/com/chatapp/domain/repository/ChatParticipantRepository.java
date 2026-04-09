package com.chatapp.domain.repository;

import com.chatapp.domain.model.ChatParticipant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, UUID> {

    boolean existsByChatRoomIdAndUserId(UUID chatRoomId, UUID userId);

    Optional<ChatParticipant> findByChatRoomIdAndUserId(UUID chatRoomId, UUID userId);

    List<ChatParticipant> findByChatRoomId(UUID chatRoomId);

    List<ChatParticipant> findByChatRoomIdAndUserIdNot(UUID chatRoomId, UUID userId);
}
