package com.fintrack.domain.port.in;

public interface ResetPasswordUseCase {

    void resetPassword(ResetPasswordCommand command);

    record ResetPasswordCommand(String token, String newPassword) {}
}
