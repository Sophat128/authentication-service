package org.example.controller;

import com.example.response.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.example.model.SmtpDto;
import org.example.model.request.SmtpRequest;
import org.example.service.EmailKbService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/configEmail")
@SecurityRequirement(name = "app")
public class EmailKBServiceController {
    private final EmailKbService emailKbService;

    public EmailKBServiceController(EmailKbService emailKbService) {
        this.emailKbService = emailKbService;
    }

    @PostMapping("/")
    public ResponseEntity<?> configEmail(@RequestBody SmtpRequest smtpRequest, Principal principal){
        SmtpDto smtp = emailKbService.configEmail(smtpRequest,principal);
        return ResponseEntity.ok(smtp);
    }
    @PutMapping("/{id}")
    public ApiResponse updateConfigEmail(@PathVariable Long id , @RequestBody SmtpRequest smtpRequest,Principal principal){
        emailKbService.updateConfigEmail(id,smtpRequest,principal);
        return ApiResponse.builder()
                .message("successfully update configuration")
                .status(200)
                .build();
    }
}
