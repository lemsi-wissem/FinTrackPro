package com.fintrack.domain.port.in;

public interface ForgotPasswordUseCase {

    void requestPasswordReset(ForgotPasswordCommand command);

    record ForgotPasswordCommand(String email) {}
}
