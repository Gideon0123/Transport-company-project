package com.example.transport.filter;

import com.example.transport.repository.VerificationTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TokenCleanupScheduler {

    private final VerificationTokenRepository repository;

    @Scheduled(fixedRate = 600000)
    @Transactional
    public void deleteExpiredTokens() {
        repository.deleteAllExpired(LocalDateTime.now());
    }
}