package com.fintrack.application.usecase;

import com.fintrack.domain.exception.UserNotFoundException;
import com.fintrack.domain.port.in.ChangePasswordUseCase;
import com.fintrack.domain.port.out.PasswordEncoderPort;
import com.fintrack.domain.port.out.UserRepositoryPort;

public class ChangePasswordUseCaseImpl implements ChangePasswordUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;

    public ChangePasswordUseCaseImpl(UserRepositoryPort userRepository, PasswordEncoderPort passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void changePassword(ChangePasswordCommand command) {
        var user = userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException(command.userId()));

        if (!passwordEncoder.matches(command.currentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.changePassword(passwordEncoder.encode(command.newPassword()));
        userRepository.save(user);
    }
}
