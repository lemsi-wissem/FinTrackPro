package com.fintrack.application.usecase;

import com.fintrack.domain.exception.InvalidTokenException;
import com.fintrack.domain.exception.UserNotFoundException;
import com.fintrack.domain.port.in.ResetPasswordUseCase;
import com.fintrack.domain.port.out.PasswordEncoderPort;
import com.fintrack.domain.port.out.PasswordResetTokenPort;
import com.fintrack.domain.port.out.UserRepositoryPort;

import java.util.UUID;

public class ResetPasswordUseCaseImpl implements ResetPasswordUseCase {

    private final PasswordResetTokenPort resetTokenPort;
    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;

    public ResetPasswordUseCaseImpl(PasswordResetTokenPort resetTokenPort,
                                    UserRepositoryPort userRepository,
                                    PasswordEncoderPort passwordEncoder) {
        this.resetTokenPort = resetTokenPort;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void resetPassword(ResetPasswordCommand command) {
        UUID userId = resetTokenPort.findUserIdByToken(command.token())
                .orElseThrow(() -> new InvalidTokenException("Password reset token is invalid or expired"));

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.changePassword(passwordEncoder.encode(command.newPassword()));
        userRepository.save(user);

        // One-time use: invalidate the token immediately after successful reset
        resetTokenPort.invalidate(command.token());
    }
}
