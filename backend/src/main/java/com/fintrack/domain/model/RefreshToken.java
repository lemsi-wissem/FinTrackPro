package com.fintrack.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class RefreshToken {

    private UUID id;
    private UUID userId;
    private String tokenHash;
    private LocalDateTime expiresAt;
    private boolean revoked;
    private LocalDateTime createdAt;

    private RefreshToken() {}

    public static RefreshToken create(UUID userId, String tokenHash, LocalDateTime expiresAt) {
        RefreshToken token = new RefreshToken();
        token.id = UUID.randomUUID();
        token.userId = userId;
        token.tokenHash = tokenHash;
        token.expiresAt = expiresAt;
        token.revoked = false;
        token.createdAt = LocalDateTime.now();
        return token;
    }

    public static RefreshToken reconstitute(UUID id, UUID userId, String tokenHash,
                                             LocalDateTime expiresAt, boolean revoked,
                                             LocalDateTime createdAt) {
        RefreshToken token = new RefreshToken();
        token.id = id;
        token.userId = userId;
        token.tokenHash = tokenHash;
        token.expiresAt = expiresAt;
        token.revoked = revoked;
        token.createdAt = createdAt;
        return token;
    }

    public void revoke() {
        this.revoked = true;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }

    public boolean isValid() {
        return !revoked && !isExpired();
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getTokenHash() { return tokenHash; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public boolean isRevoked() { return revoked; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
