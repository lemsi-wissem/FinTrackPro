package com.fintrack.domain.port.out;

import com.fintrack.domain.model.RefreshToken;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepositoryPort {
    RefreshToken save(RefreshToken token);
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    void revokeAllByUserId(UUID userId);
}
