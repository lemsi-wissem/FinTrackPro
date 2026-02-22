package com.fintrack.domain.port.out;

import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenPort {

    void store(String token, UUID userId, long ttlSeconds);

    Optional<UUID> findUserIdByToken(String token);

    void invalidate(String token);
}
