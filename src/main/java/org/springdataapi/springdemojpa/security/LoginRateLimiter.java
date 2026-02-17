package org.springdataapi.springdemojpa.security;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginRateLimiter {

    private static final int MAX_ATTEMPTS = 5;
    private static final long BLOCK_DURATION_SECONDS = 300; // 5 minutos
    private static final long EXPIRY_SECONDS = 600; // 10 minutos

    private final ConcurrentHashMap<String, LoginAttempt> attempts = new ConcurrentHashMap<>();

    public boolean isBlocked(String key) {
        LoginAttempt attempt = attempts.get(key);
        if (attempt == null) {
            return false;
        }
        if (attempt.blockedUntil != null && Instant.now().isBefore(attempt.blockedUntil)) {
            return true;
        }
        if (attempt.blockedUntil != null && Instant.now().isAfter(attempt.blockedUntil)) {
            attempts.remove(key);
            return false;
        }
        return false;
    }

    public void registerFailedAttempt(String key) {
        attempts.compute(key, (k, existing) -> {
            if (existing == null) {
                return new LoginAttempt(1, Instant.now(), null);
            }
            int newCount = existing.count + 1;
            Instant blocked = null;
            if (newCount >= MAX_ATTEMPTS) {
                blocked = Instant.now().plusSeconds(BLOCK_DURATION_SECONDS);
            }
            return new LoginAttempt(newCount, Instant.now(), blocked);
        });
    }

    public void registerSuccessfulLogin(String key) {
        attempts.remove(key);
    }

    @Scheduled(fixedRate = 600000) // cada 10 minutos
    public void cleanup() {
        Instant cutoff = Instant.now().minusSeconds(EXPIRY_SECONDS);
        attempts.entrySet().removeIf(entry -> {
            LoginAttempt attempt = entry.getValue();
            if (attempt.blockedUntil != null) {
                return Instant.now().isAfter(attempt.blockedUntil);
            }
            return attempt.lastAttempt.isBefore(cutoff);
        });
    }

    private static class LoginAttempt {
        final int count;
        final Instant lastAttempt;
        final Instant blockedUntil;

        LoginAttempt(int count, Instant lastAttempt, Instant blockedUntil) {
            this.count = count;
            this.lastAttempt = lastAttempt;
            this.blockedUntil = blockedUntil;
        }
    }
}
