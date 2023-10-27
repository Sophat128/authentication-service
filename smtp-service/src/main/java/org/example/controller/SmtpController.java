package org.example.controller;

import com.example.dto.ApplicationDto;
import com.example.dto.SmtpDto;
import com.example.response.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.example.entity.request.SmtpRequest;
import org.example.service.SmtpService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/smtp")
@SecurityRequirement(name = "auth")
public class SmtpController {
    private final SmtpService smtpService;

    public SmtpController(SmtpService smtpService) {
        this.smtpService = smtpService;
    }
    @PostMapping("/")
    public ResponseEntity<?> setUpSmtp(@RequestBody SmtpRequest smtpRequest, Principal principal , @AuthenticationPrincipal Jwt jwt, @PathVariable UUID appId){
        SmtpDto smtpDto = smtpService.setUpSmtp(smtpRequest,principal,jwt,appId);
        ApiResponse<SmtpDto> response = ApiResponse.<SmtpDto>builder()
                .message("create new application successfully")
                .status(200)
                .payload(smtpDto)
                .build();
        return ResponseEntity.ok(response);
    }

}
