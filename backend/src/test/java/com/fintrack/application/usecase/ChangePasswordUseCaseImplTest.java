package com.fintrack.application.usecase;

import com.fintrack.domain.exception.UserNotFoundException;
import com.fintrack.domain.model.User;
import com.fintrack.domain.port.in.ChangePasswordUseCase.ChangePasswordCommand;
import com.fintrack.domain.port.out.PasswordEncoderPort;
import com.fintrack.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangePasswordUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    private ChangePasswordUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new ChangePasswordUseCaseImpl(userRepository, passwordEncoder);
    }

    @Test
    void changePassword_whenCorrectCurrentPassword_shouldEncodeAndSave() {
        UUID userId = UUID.randomUUID();
        User user = User.create("alice@example.com", "oldHash", "Alice", "Smith");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPass", "oldHash")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("newHash");
        when(userRepository.save(any())).thenReturn(user);

        var command = new ChangePasswordCommand(userId, "oldPass", "newPass");
        useCase.changePassword(command);

        verify(passwordEncoder).matches("oldPass", "oldHash");
        verify(passwordEncoder).encode("newPass");
        verify(userRepository).save(user);
    }

    @Test
    void changePassword_whenWrongCurrentPassword_shouldThrowIllegalArgumentException() {
        UUID userId = UUID.randomUUID();
        User user = User.create("alice@example.com", "correctHash", "Alice", "Smith");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPass", "correctHash")).thenReturn(false);

        var command = new ChangePasswordCommand(userId, "wrongPass", "newPass");

        assertThatThrownBy(() -> useCase.changePassword(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("incorrect");

        verify(userRepository, never()).save(any());
    }

    @Test
    void changePassword_whenUserNotFound_shouldThrowUserNotFoundException() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        var command = new ChangePasswordCommand(userId, "any", "any");

        assertThatThrownBy(() -> useCase.changePassword(command))
                .isInstanceOf(UserNotFoundException.class);

        verifyNoInteractions(passwordEncoder);
        verify(userRepository, never()).save(any());
    }

    @Test
    void changePassword_shouldUpdatePasswordHashOnUser() {
        UUID userId = UUID.randomUUID();
        User user = User.create("alice@example.com", "oldHash", "Alice", "Smith");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPass", "oldHash")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("newHash");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var command = new ChangePasswordCommand(userId, "oldPass", "newPass");
        useCase.changePassword(command);

        // Verify user's password was actually mutated before save
        verify(userRepository).save(argThat(u -> "newHash".equals(u.getPasswordHash())));
    }
}
