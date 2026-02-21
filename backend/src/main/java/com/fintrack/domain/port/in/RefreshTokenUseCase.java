package com.fintrack.domain.port.in;

public interface RefreshTokenUseCase {
    LoginUserUseCase.AuthTokens refresh(String refreshToken);
}
