package com.fintrack.infrastructure.messaging;

import com.fintrack.domain.port.out.EmailSenderPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DevEmailSenderAdapter implements EmailSenderPort {

    @Value("${app.mail.verification-url}")
    private String verificationBaseUrl;

    @Override
    public void sendVerificationEmail(String to, String verificationToken) {
        String verificationLink = verificationBaseUrl + "?token=" + verificationToken;
        log.info("=== VERIFICATION EMAIL ===");
        log.info("To: {}", to);
        log.info("Link: {}", verificationLink);
        log.info("==========================");
    }
}
