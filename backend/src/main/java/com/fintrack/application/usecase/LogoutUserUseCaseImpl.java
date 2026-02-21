package com.fintrack.application.usecase;

import com.fintrack.domain.port.in.LogoutUserUseCase;
import com.fintrack.domain.port.out.RefreshTokenRepositoryPort;
import com.fintrack.domain.port.out.TokenBlacklistPort;
import com.fintrack.domain.port.out.TokenProviderPort;

public class LogoutUserUseCaseImpl implements LogoutUserUseCase {

    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final TokenBlacklistPort tokenBlacklist;
    private final TokenProviderPort tokenProvider;

    public LogoutUserUseCaseImpl(RefreshTokenRepositoryPort refreshTokenRepository,
                                  TokenBlacklistPort tokenBlacklist,
                                  TokenProviderPort tokenProvider) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenBlacklist = tokenBlacklist;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void logout(String refreshToken, String accessToken) {
        // Revoke refresh token
        String refreshTokenHash = LoginUserUseCaseImpl.hashToken(refreshToken);
        refreshTokenRepository.findByTokenHash(refreshTokenHash)
                .ifPresent(token -> {
                    token.revoke();
                    refreshTokenRepository.save(token);
                });

        // Blacklist access token
        if (accessToken != null && tokenProvider.validateToken(accessToken)) {
            String jti = tokenProvider.extractTokenId(accessToken);
            long remainingSeconds = tokenProvider.getAccessTokenRemainingSeconds(accessToken);
            if (remainingSeconds > 0) {
                tokenBlacklist.blacklist(jti, remainingSeconds);
            }
        }
    }
}
