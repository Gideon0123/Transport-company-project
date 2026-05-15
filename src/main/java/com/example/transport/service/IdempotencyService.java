package com.example.transport.service;

import com.example.transport.dto.IdempotencyRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String PREFIX = "idempotency:";

    public boolean exists(String key) {

        return Boolean.TRUE.equals(
                redisTemplate.hasKey(PREFIX + key)
        );
    }

    public void save(
            String key,
            IdempotencyRecord record
    ) {

        redisTemplate.opsForValue().set(
                PREFIX + key,
                record,
                Duration.ofHours(24)
        );
    }

    public IdempotencyRecord get(String key) {

        return (IdempotencyRecord)
                redisTemplate.opsForValue()
                        .get(PREFIX + key);
    }
}