package com.fintrack.domain.port.out;

public interface EmailSenderPort {
    void sendVerificationEmail(String to, String verificationToken);
    void sendPasswordResetEmail(String to, String resetToken);
}
