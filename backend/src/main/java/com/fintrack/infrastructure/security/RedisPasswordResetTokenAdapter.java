package com.fintrack.infrastructure.security;

import com.fintrack.domain.port.out.PasswordResetTokenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisPasswordResetTokenAdapter implements PasswordResetTokenPort {

    private final StringRedisTemplate stringRedisTemplate;
    private static final String KEY_PREFIX = "password:reset:";

    @Override
    public void store(String token, UUID userId, long ttlSeconds) {
        stringRedisTemplate.opsForValue().set(
                KEY_PREFIX + token,
                userId.toString(),
                ttlSeconds,
                TimeUnit.SECONDS
        );
    }

    @Override
    public Optional<UUID> findUserIdByToken(String token) {
        String value = stringRedisTemplate.opsForValue().get(KEY_PREFIX + token);
        return Optional.ofNullable(value).map(UUID::fromString);
    }

    @Override
    public void invalidate(String token) {
        stringRedisTemplate.delete(KEY_PREFIX + token);
    }
}
