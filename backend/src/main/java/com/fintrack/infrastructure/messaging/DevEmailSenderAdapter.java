package com.fintrack.infrastructure.messaging;

import com.fintrack.domain.port.out.EmailSenderPort;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DevEmailSenderAdapter implements EmailSenderPort {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;

    @Value("${app.mail.verification-url}")
    private String verificationBaseUrl;

    @Value("${app.mail.password-reset-url}")
    private String passwordResetBaseUrl;

    @Override
    public void sendVerificationEmail(String to, String verificationToken) {
        String link = verificationBaseUrl + "?token=" + verificationToken;
        String html = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                  <h2 style="color: #4F46E5;">Welcome to FinTrack Pro!</h2>
                  <p>Please verify your email address by clicking the button below.</p>
                  <a href="%s"
                     style="display:inline-block;padding:12px 24px;background:#4F46E5;color:#fff;
                            text-decoration:none;border-radius:6px;font-weight:bold;">
                    Verify Email
                  </a>
                  <p style="color:#6B7280;font-size:12px;margin-top:24px;">
                    Or copy this link: %s
                  </p>
                </div>
                """.formatted(link, link);
        send(to, "Verify your FinTrack Pro email", html);
    }

    @Override
    public void sendPasswordResetEmail(String to, String resetToken) {
        String link = passwordResetBaseUrl + "?token=" + resetToken;
        String html = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                  <h2 style="color: #4F46E5;">Reset your password</h2>
                  <p>Click the button below to reset your FinTrack Pro password.
                     This link expires in 1 hour.</p>
                  <a href="%s"
                     style="display:inline-block;padding:12px 24px;background:#EF4444;color:#fff;
                            text-decoration:none;border-radius:6px;font-weight:bold;">
                    Reset Password
                  </a>
                  <p style="color:#6B7280;font-size:12px;margin-top:24px;">
                    If you didn't request this, ignore this email.<br/>
                    Or copy this link: %s
                  </p>
                </div>
                """.formatted(link, link);
        send(to, "Reset your FinTrack Pro password", html);
    }

    private void send(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("Email sent to {} via MailHog — Subject: {}", to, subject);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
        }
    }
}
