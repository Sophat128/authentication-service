package org.example.service;


import com.example.dto.SmtpDto;
import org.example.entity.request.SmtpRequest;
import org.springframework.security.oauth2.jwt.Jwt;

import java.security.Principal;
import java.util.UUID;

public interface SmtpService {
    SmtpDto setUpSmtp(SmtpRequest smtpRequest, Principal principal, Jwt jwt, UUID appId);
}
