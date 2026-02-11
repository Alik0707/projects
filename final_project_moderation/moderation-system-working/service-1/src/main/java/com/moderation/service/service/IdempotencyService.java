package com.moderation.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String PROCESSED_PREFIX = "processed:event:";
    private static final String PROCESSING_PREFIX = "processing:event:";

    private static final Duration PROCESSED_TTL = Duration.ofHours(24);
    private static final Duration PROCESSING_TTL = Duration.ofMinutes(5);

    public boolean isEventProcessed(String eventId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(PROCESSED_PREFIX + eventId));
    }


    public boolean tryStartProcessing(String eventId) {
        Boolean ok = redisTemplate.opsForValue().setIfAbsent(
                PROCESSING_PREFIX + eventId, "1", PROCESSING_TTL);
        return Boolean.TRUE.equals(ok);
    }

    public void markEventAsProcessed(String eventId) {
        redisTemplate.opsForValue().set(PROCESSED_PREFIX + eventId, "1", PROCESSED_TTL);
        redisTemplate.delete(PROCESSING_PREFIX + eventId);
    }

    public void clearProcessingLock(String eventId) {
        redisTemplate.delete(PROCESSING_PREFIX + eventId);
    }
}
