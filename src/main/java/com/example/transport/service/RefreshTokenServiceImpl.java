package com.example.transport.service;

import com.example.transport.model.RefreshToken;
import com.example.transport.model.User;
import com.example.transport.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public RefreshToken create(User user) {
        RefreshToken token = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .build();

        RefreshToken savedToken = repository.save(token);

        long REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60 * 1000;
        redisTemplate.opsForValue().set(
                savedToken.getToken(),
                user.getEmail(),
                REFRESH_TOKEN_EXPIRATION,
                TimeUnit.MILLISECONDS
        );

        return savedToken;
    }

    @Override
    public RefreshToken verify(String token) {

        String email = (String) redisTemplate.opsForValue().get(token);

        if (email == null) {
            throw new RuntimeException("Invalid or expired Refresh token");
        }

        RefreshToken refreshToken = repository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid Refresh token"));

        if (refreshToken.isRevoked() || refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Expired Refresh token");
        }

        return refreshToken;
    }

    @Override
    public void revoke(RefreshToken token) {
        token.setRevoked(true);
        repository.save(token);

        redisTemplate.delete(token.getToken());
    }

    @Override
    public void delete(RefreshToken token) {
        repository.delete(token);

        redisTemplate.delete(token.getToken());
    }
}
