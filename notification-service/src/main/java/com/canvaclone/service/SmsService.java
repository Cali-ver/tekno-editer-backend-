package com.canvaclone.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    @Value("${twilio.account.sid:ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx}")
    private String accountSid;

    @Value("${twilio.auth.token:xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx}")
    private String authToken;

    @Value("${twilio.phone.number:+1234567890}")
    private String twilioPhoneNumber;

    @PostConstruct
    public void init() {
        if (!accountSid.startsWith("AC")) {
            System.err.println("Twilio SID not configured, SMS will fail.");
        } else {
            Twilio.init(accountSid, authToken);
        }
    }

    public void sendSms(String to, String body) {
        Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(twilioPhoneNumber),
                body
        ).create();
    }
}
