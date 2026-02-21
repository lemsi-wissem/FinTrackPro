package com.fintrack.infrastructure.security;

import com.fintrack.domain.port.out.TokenBlacklistPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisTokenBlacklistAdapter implements TokenBlacklistPort {

    private final StringRedisTemplate redisTemplate;
    private static final String BLACKLIST_PREFIX = "token:blacklist:";

    @Override
    public void blacklist(String tokenId, long ttlSeconds) {
        redisTemplate.opsForValue().set(
                BLACKLIST_PREFIX + tokenId, "revoked",
                ttlSeconds, TimeUnit.SECONDS
        );
    }

    @Override
    public boolean isBlacklisted(String tokenId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + tokenId));
    }
}
