package com.canvaclone.controller;

import com.canvaclone.dto.SmsRequest;
import com.canvaclone.service.SmsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sms")
public class SmsController {

    @Autowired
    private SmsService smsService;

    @PostMapping("/send")
    public ResponseEntity<?> sendSms(@Valid @RequestBody SmsRequest request) {
        try {
            smsService.sendSms(request.getPhoneNumber(), request.getMessage());
            return ResponseEntity.ok("SMS sent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to send SMS: " + e.getMessage());
        }
    }
}
