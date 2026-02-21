package com.fintrack.infrastructure.config;

import com.fintrack.application.usecase.*;
import com.fintrack.domain.port.in.*;
import com.fintrack.domain.port.out.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Value("${app.jwt.refresh-token-expiration}")
    private long refreshTokenExpirationMs;

    @Bean
    public RegisterUserUseCase registerUserUseCase(UserRepositoryPort userRepo,
                                                    PasswordEncoderPort encoder,
                                                    EmailSenderPort emailSender,
                                                    TokenProviderPort tokenProvider) {
        return new RegisterUserUseCaseImpl(userRepo, encoder, emailSender, tokenProvider);
    }

    @Bean
    public VerifyEmailUseCase verifyEmailUseCase(UserRepositoryPort userRepo,
                                                  TokenProviderPort tokenProvider) {
        return new VerifyEmailUseCaseImpl(userRepo, tokenProvider);
    }

    @Bean
    public LoginUserUseCase loginUserUseCase(UserRepositoryPort userRepo,
                                              PasswordEncoderPort encoder,
                                              TokenProviderPort tokenProvider,
                                              RefreshTokenRepositoryPort refreshTokenRepo) {
        return new LoginUserUseCaseImpl(userRepo, encoder, tokenProvider, refreshTokenRepo, refreshTokenExpirationMs);
    }

    @Bean
    public RefreshTokenUseCase refreshTokenUseCase(RefreshTokenRepositoryPort refreshTokenRepo,
                                                    UserRepositoryPort userRepo,
                                                    TokenProviderPort tokenProvider) {
        return new RefreshTokenUseCaseImpl(refreshTokenRepo, userRepo, tokenProvider, refreshTokenExpirationMs);
    }

    @Bean
    public LogoutUserUseCase logoutUserUseCase(RefreshTokenRepositoryPort refreshTokenRepo,
                                                TokenBlacklistPort blacklist,
                                                TokenProviderPort tokenProvider) {
        return new LogoutUserUseCaseImpl(refreshTokenRepo, blacklist, tokenProvider);
    }

    @Bean
    public GetCurrentUserUseCase getCurrentUserUseCase(UserRepositoryPort userRepo) {
        return new GetCurrentUserUseCaseImpl(userRepo);
    }
}
