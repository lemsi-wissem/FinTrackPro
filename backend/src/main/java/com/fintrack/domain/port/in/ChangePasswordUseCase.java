package com.fintrack.domain.port.in;

import java.util.UUID;

public interface ChangePasswordUseCase {

    void changePassword(ChangePasswordCommand command);

    record ChangePasswordCommand(UUID userId, String currentPassword, String newPassword) {}
}
