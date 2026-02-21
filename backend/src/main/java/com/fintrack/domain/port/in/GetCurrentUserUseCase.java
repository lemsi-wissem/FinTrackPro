package com.fintrack.domain.port.in;

import com.fintrack.domain.model.User;

import java.util.UUID;

public interface GetCurrentUserUseCase {
    User getCurrentUser(UUID userId);
}
