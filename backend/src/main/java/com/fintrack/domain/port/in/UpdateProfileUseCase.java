package com.fintrack.domain.port.in;

import com.fintrack.domain.model.User;

import java.util.UUID;

public interface UpdateProfileUseCase {

    User updateProfile(UpdateProfileCommand command);

    record UpdateProfileCommand(UUID userId, String firstName, String lastName) {}
}
