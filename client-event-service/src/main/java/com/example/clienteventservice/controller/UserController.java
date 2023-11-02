package com.example.clienteventservice.controller;

import com.example.clienteventservice.model.request.ProfileRequest;
import com.example.clienteventservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.UUID;


@RestController
@CrossOrigin
@RequestMapping("api/v1/clients")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

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
    @GetMapping
    @SecurityRequirement(name = "auth")
    @Operation(summary = "get user information ")
    public ResponseEntity<?> getUserInfo(Principal principal) {
        return ResponseEntity.ok().body(userService.getInfo(principal));
    }

    @PutMapping
    @SecurityRequirement(name = "auth")
    @Operation(summary = "update information user current user (token) ")
    public ResponseEntity<?> updateById(@RequestBody ProfileRequest userRequest, Principal principal, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok().body(userService.updateById(userRequest, principal,jwt));
    }


//    @PostMapping("/platform")
//    @SecurityRequirement(name = "auth")
//    public ResponseEntity<?> chooseNotificationTypes(
//            @RequestParam NotificationType notificationType,
//            Principal principal,
//            HttpServletResponse response
//    ) {
//        try {
//            if (principal == null) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Authentication required");
//            }
//
//            if (notificationType == NotificationType.TELEGRAM) {
//                userService.chooseNotificationTypes(notificationType, principal);
//                // Redirect the user to the Telegram bot URL
//                String telegramBotURL = "https://t.me/FintrackAPIBot?start=" + principal.getName();
//                response.sendRedirect(telegramBotURL);
//            } else {
//                // Handle other notification types
//                userService.chooseNotificationTypes(notificationType, principal);
//            }
//
//            return ResponseEntity.ok().body("Notification type chosen successfully");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error occurred while processing the request: " + e.getMessage());
//        }
//    }


    @PostMapping("/send-money")
    @SecurityRequirement(name = "auth")
    public ResponseEntity<?> sendMoney(@RequestBody BigDecimal sendMoney, Principal principal) {
        return ResponseEntity.ok().body(userService.sendMoney(String.valueOf(sendMoney), principal));
    }
}
