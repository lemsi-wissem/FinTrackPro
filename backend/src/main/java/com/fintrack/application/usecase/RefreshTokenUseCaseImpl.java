package com.fintrack.application.usecase;

import com.fintrack.domain.exception.InvalidTokenException;
import com.fintrack.domain.exception.UserNotFoundException;
import com.fintrack.domain.model.RefreshToken;
import com.fintrack.domain.model.User;
import com.fintrack.domain.port.in.LoginUserUseCase.AuthTokens;
import com.fintrack.domain.port.in.RefreshTokenUseCase;
import com.fintrack.domain.port.out.RefreshTokenRepositoryPort;
import com.fintrack.domain.port.out.TokenProviderPort;
import com.fintrack.domain.port.out.UserRepositoryPort;

import java.time.LocalDateTime;

public class RefreshTokenUseCaseImpl implements RefreshTokenUseCase {

    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final UserRepositoryPort userRepository;
    private final TokenProviderPort tokenProvider;
    private final long refreshTokenExpirationMs;

    public RefreshTokenUseCaseImpl(RefreshTokenRepositoryPort refreshTokenRepository,
                                    UserRepositoryPort userRepository,
                                    TokenProviderPort tokenProvider,
                                    long refreshTokenExpirationMs) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    @Override
    public AuthTokens refresh(String rawRefreshToken) {
        String tokenHash = LoginUserUseCaseImpl.hashToken(rawRefreshToken);

        RefreshToken storedToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        if (!storedToken.isValid()) {
            throw new InvalidTokenException("Refresh token is expired or revoked");
        }

        // Revoke old token (token rotation)
        storedToken.revoke();
        refreshTokenRepository.save(storedToken);

        User user = userRepository.findById(storedToken.getUserId())
                .orElseThrow(() -> new UserNotFoundException(storedToken.getUserId()));

        // Generate new token pair
        String newAccessToken = tokenProvider.generateAccessToken(user.getId(), user.getEmail(), user.getRole());
        String newRawRefreshToken = tokenProvider.generateRefreshToken();
        String newRefreshTokenHash = LoginUserUseCaseImpl.hashToken(newRawRefreshToken);

        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(refreshTokenExpirationMs / 1000);
        RefreshToken newRefreshToken = RefreshToken.create(user.getId(), newRefreshTokenHash, expiresAt);
        refreshTokenRepository.save(newRefreshToken);

        return new AuthTokens(newAccessToken, newRawRefreshToken);
    }
}
