package com.example.services;

import com.example.models.Email;
import com.example.models.EmailConfig;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.messaging.MessagingException;
import org.springframework.security.oauth2.jwt.Jwt;

import java.io.IOException;
import java.security.Principal;
import java.util.UUID;

public interface EmailSenderService {
//    void sendEmail(Email email);
    void sendConfirmationEmail(Email email, UUID smtpId,UUID appId, Jwt jwt) throws MessagingException, IOException, jakarta.mail.MessagingException;

    EmailConfig cofigMailSender(UUID smtpId, UUID appId, Principal principal, Jwt jwt);

//    JavaMailSenderImpl setSender(UUID smtpId, UUID appId, Principal principal, Jwt jwt);
}
