package com.canvaclone.controller;

import com.canvaclone.dto.EmailRequest;
import com.canvaclone.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(@Valid @RequestBody EmailRequest request) {
        try {
            emailService.sendDesignEmail(request.getEmail(), request.getMessage());
            return ResponseEntity.ok("Email sent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to send email: " + e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<Void> sendVerificationEmail(
            @RequestParam("email") String email,
            @RequestParam("token") String token) {
        emailService.sendVerificationEmail(email, token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/otp")
    public ResponseEntity<Void> sendOtpEmail(
            @RequestParam("email") String email,
            @RequestParam("otp") String otp) {
        emailService.sendOtpEmail(email, otp);
        return ResponseEntity.ok().build();
    }
}
