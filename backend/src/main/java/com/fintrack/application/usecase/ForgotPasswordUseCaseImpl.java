package com.fintrack.application.usecase;

import com.fintrack.domain.port.in.ForgotPasswordUseCase;
import com.fintrack.domain.port.out.EmailSenderPort;
import com.fintrack.domain.port.out.PasswordResetTokenPort;
import com.fintrack.domain.port.out.UserRepositoryPort;

import java.util.UUID;

public class ForgotPasswordUseCaseImpl implements ForgotPasswordUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordResetTokenPort resetTokenPort;
    private final EmailSenderPort emailSender;
    private final long resetTokenTtlSeconds;

    public ForgotPasswordUseCaseImpl(UserRepositoryPort userRepository,
                                     PasswordResetTokenPort resetTokenPort,
                                     EmailSenderPort emailSender,
                                     long resetTokenTtlSeconds) {
        this.userRepository = userRepository;
        this.resetTokenPort = resetTokenPort;
        this.emailSender = emailSender;
        this.resetTokenTtlSeconds = resetTokenTtlSeconds;
    }

    @Override
    public void requestPasswordReset(ForgotPasswordCommand command) {
        // Always succeeds to prevent email enumeration attacks
        userRepository.findByEmail(command.email()).ifPresent(user -> {
            if (user.isActive() && user.isVerified()) {
                String token = UUID.randomUUID().toString();
                resetTokenPort.store(token, user.getId(), resetTokenTtlSeconds);
                emailSender.sendPasswordResetEmail(user.getEmail(), token);
            }
        });
    }
}
