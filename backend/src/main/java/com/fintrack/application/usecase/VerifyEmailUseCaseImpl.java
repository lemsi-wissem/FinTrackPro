package com.fintrack.application.usecase;

import com.fintrack.domain.exception.InvalidTokenException;
import com.fintrack.domain.exception.UserNotFoundException;
import com.fintrack.domain.model.User;
import com.fintrack.domain.port.in.VerifyEmailUseCase;
import com.fintrack.domain.port.out.TokenProviderPort;
import com.fintrack.domain.port.out.UserRepositoryPort;

import java.util.UUID;

public class VerifyEmailUseCaseImpl implements VerifyEmailUseCase {

    private final UserRepositoryPort userRepository;
    private final TokenProviderPort tokenProvider;

    public VerifyEmailUseCaseImpl(UserRepositoryPort userRepository, TokenProviderPort tokenProvider) {
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void verify(String token) {
        if (!tokenProvider.validateToken(token)) {
            throw new InvalidTokenException("Invalid or expired verification token");
        }

        UUID userId = tokenProvider.extractUserId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.verify();
        userRepository.save(user);
    }
}
