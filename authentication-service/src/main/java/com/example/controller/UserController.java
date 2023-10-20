package com.example.controller;

import com.example.exception.BadRequestException;
import com.example.exception.ForbiddenException;
import com.example.model.request.ProfileRequest;
import com.example.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.RSATokenVerifier;
import org.keycloak.TokenVerifier;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.AccessToken;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;


@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("username")
    @Operation(summary = "get user by username")
    public ResponseEntity<?> getByUsername(@RequestParam String username) {
        return ResponseEntity.ok().body(userService.getByUserName(username));
    }

    @GetMapping("email")
    @Operation(summary = "get user by email")
    public ResponseEntity<?> getByEmail(@RequestParam String email) {
        return ResponseEntity.ok().body(userService.getByEmail(email));
    }

//    @GetMapping
    public ResponseEntity<?> getAllUser() {
        return ResponseEntity.ok().body(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "get user by id (UUID) ")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        return ResponseEntity.ok().body(userService.getById(id));
    }

    @PutMapping
    @SecurityRequirement(name = "auth")
    @Operation(summary = "update information user current user (token) ")
    public ResponseEntity<?> updateById(@RequestBody ProfileRequest userRequest, Principal principal) {
        return ResponseEntity.ok().body(userService.updateById(userRequest, principal));

    }
}
