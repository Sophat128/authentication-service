package org.example.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.example.model.PlatformType;
import org.example.model.request.ApplicationRequest;
import org.example.service.ApplicationService;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/application")
@SecurityRequirement(name = "auth")
public class ApplicationController {
    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }


    @PostMapping("/create")
    public ResponseEntity<?> createNewApp(@RequestBody ApplicationRequest applicationRequest, @RequestParam PlatformType platformType, Principal principal){
        ApplicationDto applicationDto = applicationService.createNewApp(applicationRequest,platformType,principal);
        ApiResponse<ApplicationDto> response = ApiResponse.<ApplicationDto>builder()
                .message("create new application successfully")
                .status(200)
                .payload(applicationDto)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllApp(Principal principal){
        List<ApplicationDto> applications = applicationService.getAllApp(principal);
        ApiResponse<List<ApplicationDto>> response = ApiResponse.<List<ApplicationDto>>builder()
                .message("fetch application successfully")
                .status(200)
                .payload(applications)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAppById(@PathVariable UUID id , Principal principal){
        ApplicationDto applicationDto = applicationService.getApplicationById(id,principal);
        ApiResponse<ApplicationDto> response = ApiResponse.<ApplicationDto>builder()
                .message("fetch application successfully")
                .status(200)
                .payload(applicationDto)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public String deleteApplication(@PathVariable UUID id, Principal principal){
        return applicationService.deleteApp(id,principal);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAppByid(@PathVariable UUID id,@RequestBody ApplicationRequest applicationRequest, Principal principal){
        ApplicationDto applicationDto = applicationService.updateApp(applicationRequest,id,principal);
        ApiResponse<ApplicationDto> response = ApiResponse.<ApplicationDto>builder()
                .message("update application successfully")
                .status(200)
                .payload(applicationDto)
                .build();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/type/")
    public ResponseEntity<?> getAppByPlatformType(@RequestParam String platformType , Principal principal){
        List<ApplicationDto> applicationDto = applicationService.getApplicationByType(platformType,principal);
        ApiResponse<List<ApplicationDto>> response = ApiResponse.<List<ApplicationDto>>builder()
                .message("fetch application successfully")
                .status(200)
                .payload(applicationDto)
                .build();
        return ResponseEntity.ok(response);
    }

}