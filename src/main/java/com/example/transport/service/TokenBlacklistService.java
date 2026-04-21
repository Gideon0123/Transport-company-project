package com.example.transport.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final StringRedisTemplate redisTemplate;

    public void blacklist(String token, long expiry) {
        redisTemplate.opsForValue()
                .set(token, "blacklisted", Duration.ofMillis(expiry));
    }

    public boolean isBlacklisted(String token) {
        return redisTemplate.hasKey(token);
    }
}
