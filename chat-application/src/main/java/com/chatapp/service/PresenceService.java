package com.chatapp.service;

import com.chatapp.api.dto.PresenceResponse;
import com.chatapp.domain.model.User;
import com.chatapp.domain.repository.UserRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PresenceService {

    private static final Duration PRESENCE_TTL = Duration.ofHours(12);

    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public void markOnlineByEmail(String email) {
        User user = userRepository.findByEmail(email.toLowerCase())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setOnline(true);
        userRepository.save(user);
        runRedisSafely(() -> redisTemplate.opsForValue().set(presenceKey(user.getId()), true, PRESENCE_TTL));
        publishPresence(user);
    }

    @Transactional
    public void markOfflineByEmail(String email) {
        User user = userRepository.findByEmail(email.toLowerCase())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setOnline(false);
        user.setLastSeen(Instant.now());
        userRepository.save(user);
        runRedisSafely(() -> redisTemplate.delete(presenceKey(user.getId())));
        publishPresence(user);
    }

    public PresenceResponse getPresence(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        boolean online = Boolean.TRUE.equals(readRedisSafely(() -> redisTemplate.opsForValue().get(presenceKey(userId))))
            || user.isOnline();
        return new PresenceResponse(user.getId(), user.getUsername(), online, user.getLastSeen());
    }

    private void publishPresence(User user) {
        PresenceResponse response = new PresenceResponse(user.getId(), user.getUsername(), user.isOnline(), user.getLastSeen());
        messagingTemplate.convertAndSend("/topic/presence." + user.getId(), response);
    }

    private String presenceKey(UUID userId) {
        return "presence:user:" + userId;
    }

    private void runRedisSafely(Runnable action) {
        try {
            action.run();
        } catch (DataAccessException ignored) {
            // Presence still works from the database if Redis is unavailable.
        }
    }

    private Object readRedisSafely(RedisSupplier supplier) {
        try {
            return supplier.get();
        } catch (DataAccessException ignored) {
            return null;
        }
    }

    @FunctionalInterface
    private interface RedisSupplier {
        Object get();
    }
}
