package com.example.controller;


import com.example.models.Email;
import com.example.models.EmailConfig;
import com.example.models.EmailRequest;
import com.example.services.EmailSenderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sendEmail")
@SecurityRequirement(name = "app")
public class EmailController {
    private final EmailSenderService emailSenderService;

    public EmailController(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }

//    @GetMapping("/{smtpId}/{appId}")
    public ResponseEntity<?> getSmtp(@PathVariable UUID smtpId , @PathVariable UUID appId, Principal principal, @AuthenticationPrincipal Jwt jwt){
        EmailConfig emailConfig = emailSenderService.cofigMailSender(smtpId,appId,principal,jwt);
        return ResponseEntity.ok(emailConfig);
    }

    @PostMapping("/{smtpId}/{appId}")
    @Operation(summary = "for props we do not input it")
    public ResponseEntity<?> sendMail(@PathVariable UUID smtpId , @PathVariable UUID appId, Principal principal, @AuthenticationPrincipal Jwt jwt, Email email) throws MessagingException, IOException {
        emailSenderService.sendConfirmationEmail(email,smtpId,appId,jwt);
        return ResponseEntity.ok("successfully");
    }



//    @GetMapping("/getSender/{smtpId}/{appId}")
//    public ResponseEntity<?> getSender(@PathVariable UUID smtpId , @PathVariable UUID appId, Principal principal, @AuthenticationPrincipal Jwt jwt){
//        JavaMailSenderImpl javaMailSender = emailSenderService.setSender(smtpId,appId,principal,jwt);
//        return ResponseEntity.ok(javaMailSender);
//    }


}
