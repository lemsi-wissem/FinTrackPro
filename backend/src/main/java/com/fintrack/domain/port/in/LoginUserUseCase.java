package com.fintrack.domain.port.in;

public interface LoginUserUseCase {

    AuthTokens login(LoginCommand command);

    record LoginCommand(String email, String password) {}

    record AuthTokens(String accessToken, String refreshToken) {}
}
