package com.canvaclone.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "notification-service")
public interface NotificationClient {

    @PostMapping("/api/email/verify")
    void sendVerificationEmail(@RequestParam("email") String email, @RequestParam("token") String token);

    @PostMapping("/api/email/otp")
    void sendOtpEmail(@RequestParam("email") String email, @RequestParam("otp") String otp);
}
