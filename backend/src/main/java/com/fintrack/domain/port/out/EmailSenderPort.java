package com.fintrack.domain.port.out;

public interface EmailSenderPort {
    void sendVerificationEmail(String to, String verificationToken);
}
