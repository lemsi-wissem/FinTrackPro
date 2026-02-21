package com.fintrack.application.usecase;

import com.fintrack.domain.exception.UserNotFoundException;
import com.fintrack.domain.model.User;
import com.fintrack.domain.port.in.GetCurrentUserUseCase;
import com.fintrack.domain.port.out.UserRepositoryPort;

import java.util.UUID;

public class GetCurrentUserUseCaseImpl implements GetCurrentUserUseCase {

    private final UserRepositoryPort userRepository;

    public GetCurrentUserUseCaseImpl(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getCurrentUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
