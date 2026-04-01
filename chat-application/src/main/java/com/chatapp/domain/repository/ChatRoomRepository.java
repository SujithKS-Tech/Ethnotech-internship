package com.chatapp.domain.repository;

import com.chatapp.domain.model.ChatRoom;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, UUID> {

    @Query("""
        select distinct cr from ChatRoom cr
        join fetch cr.participants cp
        join fetch cp.user u
        where cp.user.id = :userId
        order by cr.updatedAt desc
        """)
    List<ChatRoom> findAllForUser(@Param("userId") UUID userId);

    @Query("""
        select distinct cr from ChatRoom cr
        join cr.participants firstParticipant
        join cr.participants secondParticipant
        where cr.chatType = com.chatapp.domain.model.ChatType.PRIVATE
          and firstParticipant.user.id = :firstUserId
          and secondParticipant.user.id = :secondUserId
        """)
    Optional<ChatRoom> findPrivateChatBetweenUsers(@Param("firstUserId") UUID firstUserId,
                                                   @Param("secondUserId") UUID secondUserId);
}
