package com.fintrack.domain.port.out;

import com.fintrack.domain.model.Role;

import java.util.UUID;

public interface TokenProviderPort {
    String generateAccessToken(UUID userId, String email, Role role);
    String generateRefreshToken();
    String generateVerificationToken(UUID userId);
    UUID extractUserId(String token);
    boolean validateToken(String token);
    String extractTokenId(String token);
    long getAccessTokenRemainingSeconds(String token);
}
