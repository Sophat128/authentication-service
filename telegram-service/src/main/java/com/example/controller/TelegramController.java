package com.example.controller;


import com.example.service.TelegramBotService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;

@RestController
@RequestMapping("api/v1/telegram")
@CrossOrigin
@SecurityRequirement(name = "auth")
public class TelegramController {
    private final TelegramBotService telegramBotService;

    public TelegramController(TelegramBotService telegramBotService) {
        this.telegramBotService = telegramBotService;
    }

    @GetMapping("/register")
    public RedirectView register(Principal principal){
        System.out.println("register");
        telegramBotService.getCurrentId(principal);
        return new RedirectView("https://t.me/FintrackAPIBot");
    }

    @PostMapping("/send-message")
    public ResponseEntity<?> sendMessage(@RequestParam String message, Principal principal){
        return ResponseEntity.ok().body(telegramBotService.sendMessage(message,principal));
    }

    @GetMapping("/test")
    public String test(){
        System.out.println("test");
        return "test";
    }

}
