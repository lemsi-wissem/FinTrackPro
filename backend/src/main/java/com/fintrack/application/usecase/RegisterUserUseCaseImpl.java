package com.fintrack.application.usecase;

import com.fintrack.domain.exception.UserAlreadyExistsException;
import com.fintrack.domain.model.User;
import com.fintrack.domain.port.in.RegisterUserUseCase;
import com.fintrack.domain.port.out.EmailSenderPort;
import com.fintrack.domain.port.out.PasswordEncoderPort;
import com.fintrack.domain.port.out.TokenProviderPort;
import com.fintrack.domain.port.out.UserRepositoryPort;

public class RegisterUserUseCaseImpl implements RegisterUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final EmailSenderPort emailSender;
    private final TokenProviderPort tokenProvider;

    public RegisterUserUseCaseImpl(UserRepositoryPort userRepository,
                                    PasswordEncoderPort passwordEncoder,
                                    EmailSenderPort emailSender,
                                    TokenProviderPort tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailSender = emailSender;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public User register(RegisterUserCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new UserAlreadyExistsException(command.email());
        }

        String encodedPassword = passwordEncoder.encode(command.password());
        User user = User.create(command.email(), encodedPassword, command.firstName(), command.lastName());
        User savedUser = userRepository.save(user);

        String verificationToken = tokenProvider.generateVerificationToken(savedUser.getId());
        emailSender.sendVerificationEmail(savedUser.getEmail(), verificationToken);

        return savedUser;
    }
}
