package com.fintrack.application.usecase;

import com.fintrack.domain.exception.UserNotFoundException;
import com.fintrack.domain.model.User;
import com.fintrack.domain.port.in.UpdateProfileUseCase.UpdateProfileCommand;
import com.fintrack.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateProfileUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepository;

    private UpdateProfileUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new UpdateProfileUseCaseImpl(userRepository);
    }

    @Test
    void updateProfile_whenUserExists_shouldUpdateAndReturnSavedUser() {
        UUID userId = UUID.randomUUID();
        User existing = User.create("alice@example.com", "hash", "Alice", "Old");
        User saved = User.create("alice@example.com", "hash", "Alice", "New");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existing));
        when(userRepository.save(any())).thenReturn(saved);

        var command = new UpdateProfileCommand(userId, "Alice", "New");
        User result = useCase.updateProfile(command);

        assertThat(result).isNotNull();
        verify(userRepository).findById(userId);
        verify(userRepository).save(existing);
    }

    @Test
    void updateProfile_whenUserNotFound_shouldThrowUserNotFoundException() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        var command = new UpdateProfileCommand(userId, "Alice", "Smith");

        assertThatThrownBy(() -> useCase.updateProfile(command))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void updateProfile_shouldMutateUserBeforeSaving() {
        UUID userId = UUID.randomUUID();
        User user = User.create("test@example.com", "hash", "Old", "Name");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var command = new UpdateProfileCommand(userId, "New", "Name");
        useCase.updateProfile(command);

        assertThat(user.getFirstName()).isEqualTo("New");
        assertThat(user.getLastName()).isEqualTo("Name");
    }
}
