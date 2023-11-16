package com.example.clienteventservice.controller;

import com.example.clienteventservice.config.WebClientConfig;
import com.example.clienteventservice.domain.dto.TelegramCreatedBotDto;
import com.example.clienteventservice.domain.request.ProfileRequest;
import com.example.clienteventservice.domain.type.NotificationType;
import com.example.clienteventservice.service.UserService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.util.UUID;


@RestController
@CrossOrigin
@RequestMapping("api/v1/customers")
@AllArgsConstructor
public class CustomerController {

    private final UserService userService;

    private final WebClientConfig webClientConfig;

    @GetMapping("username")
    @Operation(summary = "get user by username")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK."),
            @ApiResponse(code = 400, message = "Bad Request."),
            @ApiResponse(code = 500, message = "Internal Error.")
    })
    public ResponseEntity<?> getByUsername(@RequestParam String username) {
        return ResponseEntity.ok().body(userService.getByUserName(username));
    }

    @GetMapping("email")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK."),
            @ApiResponse(code = 400, message = "Bad Request."),
            @ApiResponse(code = 500, message = "Internal Error.")
    })
    @Operation(summary = "get user by email")
    public ResponseEntity<?> getByEmail(@RequestParam String email) {
        return ResponseEntity.ok().body(userService.getByEmail(email));
    }

    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK."),
            @ApiResponse(code = 400, message = "Bad Request."),
            @ApiResponse(code = 500, message = "Internal Error.")
    })
    public ResponseEntity<?> getAllUser() {
        return ResponseEntity.ok().body(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "get user by id (UUID) ")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK."),
            @ApiResponse(code = 400, message = "Bad Request."),
            @ApiResponse(code = 500, message = "Internal Error.")
    })
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        return ResponseEntity.ok().body(userService.getById(id));
    }
    @GetMapping("/current_user")
    @SecurityRequirement(name = "auth")
    @Operation(summary = "get user information ")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK."),
            @ApiResponse(code = 400, message = "Bad Request."),
            @ApiResponse(code = 500, message = "Internal Error.")
    })
    public ResponseEntity<?> getUserInfo(Principal principal) {
        return ResponseEntity.ok().body(userService.getInfo(principal));
    }

    @PutMapping
    @SecurityRequirement(name = "auth")
    @Operation(summary = "update information user current user (token) ")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK."),
            @ApiResponse(code = 400, message = "Bad Request."),
            @ApiResponse(code = 500, message = "Internal Error.")
    })
    public ResponseEntity<?> updateById(@RequestBody ProfileRequest userRequest, Principal principal, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok().body(userService.updateById(userRequest, principal,jwt));
    }


    @PostMapping("/notification-type")
//    @SecurityRequirement(name = "auth")
//    @Operation(summary = "current user (token) ")
    public ResponseEntity<?> chooseNotificationTypes(
            @RequestParam NotificationType notificationType
    ) {
        if (notificationType == NotificationType.TELEGRAM) {

            Long botId = 1L; //we use only this bot id to send notifications;
            String getBotByIdUrl = "http://telegram-service/api/v1/telegram/bots/get-bot-by-botId";
            WebClient web = webClientConfig.webClientBuilder().baseUrl(getBotByIdUrl).build();

              TelegramCreatedBotDto telegramCreatedBotDto = web.get()
                    .uri("/{botId}", botId)
                    .retrieve()
                      .bodyToMono(TelegramCreatedBotDto.class)
                    .block();

            String telegramChatUrl = telegramCreatedBotDto.getBotLink();;

            // Redirect the user to the Telegram chat URL
            RedirectView redirectView = new RedirectView(telegramChatUrl);

            // Check if the response is a redirect
            if (redirectView.isRedirectView()) {
                System.out.println("botUsername: " + telegramCreatedBotDto.getBotUsername());
                System.out.println("botToken: " + telegramCreatedBotDto.getBotToken());
                System.out.println("redirect: " +  redirectView.getUrl());
                return new ResponseEntity<>(HttpStatus.FOUND); // HTTP 302 Found (redirect)
            }
        }

        // Handle other notification types or provide a default response
        return new ResponseEntity<>("Some response data", HttpStatus.OK);
    }


}
