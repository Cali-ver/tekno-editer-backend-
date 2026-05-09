package com.canvaclone.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@canvaclone.com}")
    private String fromEmail;

    @Value("${app.backend.url}")
    private String backendUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String toEmail, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Verify your Canva Clone email");
        String verificationUrl = backendUrl + "/api/auth/verify?token=" + token;
        message.setText("Click the link below to verify your email:\n" + verificationUrl);
        try {
            mailSender.send(message);
            log.info("Verification email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send verification email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Could not send verification email", e);
        }
    }

    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Your Canva Clone Password Reset OTP");
        message.setText("Your OTP for password reset is: " + otp + "\nThis OTP is valid for a short time.");
        try {
            mailSender.send(message);
            log.info("OTP email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send OTP email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Could not send OTP email", e);
        }
    }

    public void sendDesignEmail(String toEmail, String messageText) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("A design has been shared with you from CanvaClone");
        message.setText(messageText);
        try {
            mailSender.send(message);
            log.info("Design email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send design email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send design email. Please check mail configuration.", e);
        }
    }
}
