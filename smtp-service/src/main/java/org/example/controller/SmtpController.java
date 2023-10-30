package org.example.controller;

import com.example.dto.ApplicationDto;
import com.example.dto.SmtpDto;
import com.example.response.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.example.entity.request.SmtpRequest;
import org.example.exception.BadRequestException;
import org.example.service.SmtpService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/smtp")
@SecurityRequirement(name = "app")
public class SmtpController {
    private final SmtpService smtpService;

    public SmtpController(SmtpService smtpService) {
        this.smtpService = smtpService;
    }
    @PostMapping("/{appId}")
    public ResponseEntity<?> setUpSmtp( @RequestBody SmtpRequest smtpRequest, Principal principal , @AuthenticationPrincipal Jwt jwt, @PathVariable UUID appId){
        SmtpDto smtpDto = smtpService.setUpSmtp(smtpRequest,principal,jwt,appId);
        if (smtpRequest.getUsername().isBlank()){
            throw new BadRequestException("Field username can't be blank");
        }
        if (!smtpRequest.getUsername().matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+\\.[A-Za-z]{2,6}")) {
            throw new BadRequestException(
                    "Username should be like this -> somthing@somthing.com"
            );
        }
        if (smtpRequest.getPassword().isBlank()){
            throw new BadRequestException("Field password can't be blank");
        }
        ApiResponse<SmtpDto> response = ApiResponse.<SmtpDto>builder()
                .message("setup SMTP successfully")
                .status(200)
                .payload(smtpDto)
                .build();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}/{appId}")
    public ResponseEntity<?> getSmtpById(@PathVariable UUID id,@PathVariable UUID appId,Principal principal, @AuthenticationPrincipal Jwt jwt ){
        SmtpDto smtpDto = smtpService.getSmtpById(id,appId,principal,jwt);
        ApiResponse<SmtpDto> response = ApiResponse.<SmtpDto>builder()
                .message("get SMTP by id successfully")
                .status(200)
                .payload(smtpDto)
                .build();
        return ResponseEntity.ok(response);
    }
    @PutMapping("/{id}/{appId}")
    public ResponseEntity<?> updateSmtpById(@PathVariable UUID id,@PathVariable UUID appId,@RequestBody SmtpRequest smtpRequest ,Principal principal,@AuthenticationPrincipal Jwt jwt){
        SmtpDto smtpDto = smtpService.updateSmtpById(id,appId,smtpRequest,principal,jwt);
        if (smtpRequest.getUsername().isBlank()){
            throw new BadRequestException("Field username can't be blank");
        }
        if (!smtpRequest.getUsername().matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+\\.[A-Za-z]{2,6}")) {
            throw new BadRequestException(
                    "Username should be like this -> somthing@somthing.com"
            );
        }
        if (smtpRequest.getPassword().isBlank()){
            throw new BadRequestException("Field password can't be blank");
        }
        ApiResponse<SmtpDto> response = ApiResponse.<SmtpDto>builder()
                .message("update SMTP successfully")
                .status(200)
                .payload(smtpDto)
                .build();
        return ResponseEntity.ok(response);

    }

}
