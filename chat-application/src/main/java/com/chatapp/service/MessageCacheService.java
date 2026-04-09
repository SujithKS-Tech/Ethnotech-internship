package com.chatapp.service;

import com.chatapp.api.dto.MessageResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageCacheService {

    private static final Duration HISTORY_TTL = Duration.ofHours(6);
    private static final long HISTORY_LIMIT = 50;

    private final RedisTemplate<String, Object> redisTemplate;

    public void cacheMessage(MessageResponse message) {
        String key = cacheKey(message.chatRoomId());
        runRedisSafely(() -> {
            redisTemplate.opsForList().rightPush(key, message);
            redisTemplate.opsForList().trim(key, -HISTORY_LIMIT, -1);
            redisTemplate.expire(key, HISTORY_TTL);
        });
    }

    public void overwriteHistory(UUID chatRoomId, List<MessageResponse> messages) {
        String key = cacheKey(chatRoomId);
        runRedisSafely(() -> {
            redisTemplate.delete(key);
            if (!messages.isEmpty()) {
                redisTemplate.opsForList().rightPushAll(key, new ArrayList<>(messages));
                redisTemplate.expire(key, HISTORY_TTL);
            }
        });
    }

    public List<MessageResponse> getRecentMessages(UUID chatRoomId) {
        List<Object> cached = readRedisSafely(() -> redisTemplate.opsForList().range(cacheKey(chatRoomId), 0, -1));
        if (cached == null || cached.isEmpty()) {
            return List.of();
        }
        return cached.stream()
            .filter(MessageResponse.class::isInstance)
            .map(MessageResponse.class::cast)
            .toList();
    }

    private String cacheKey(UUID chatRoomId) {
        return "chat:history:" + chatRoomId;
    }

    private void runRedisSafely(Runnable action) {
        try {
            action.run();
        } catch (DataAccessException ignored) {
            // Message history still works from MySQL if Redis is unavailable.
        }
    }

    private List<Object> readRedisSafely(RedisListSupplier supplier) {
        try {
            return supplier.get();
        } catch (DataAccessException ignored) {
            return List.of();
        }
    }

    @FunctionalInterface
    private interface RedisListSupplier {
        List<Object> get();
    }
}
