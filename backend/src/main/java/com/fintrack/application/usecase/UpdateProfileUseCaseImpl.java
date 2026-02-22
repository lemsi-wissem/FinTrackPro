package com.fintrack.application.usecase;

import com.fintrack.domain.exception.UserNotFoundException;
import com.fintrack.domain.model.User;
import com.fintrack.domain.port.in.UpdateProfileUseCase;
import com.fintrack.domain.port.out.UserRepositoryPort;

public class UpdateProfileUseCaseImpl implements UpdateProfileUseCase {

    private final UserRepositoryPort userRepository;

    public UpdateProfileUseCaseImpl(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User updateProfile(UpdateProfileCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException(command.userId()));
        user.updateProfile(command.firstName(), command.lastName());
        return userRepository.save(user);
    }
}
