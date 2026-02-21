package com.fintrack.domain.port.in;

public interface LogoutUserUseCase {
    void logout(String refreshToken, String accessToken);
}
