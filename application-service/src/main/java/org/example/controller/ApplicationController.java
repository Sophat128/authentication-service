package org.example.controller;

import org.example.ApplicationService;
import org.example.model.request.ApplicationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/application")
public class ApplicationController {
    
    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }


    @PostMapping("/create")
    public ResponseEntity<?> createNewApp(@RequestBody ApplicationRequest applicationRequest, @AuthenticationPrincipal Jwt jwt){
        return null;
    }


}
