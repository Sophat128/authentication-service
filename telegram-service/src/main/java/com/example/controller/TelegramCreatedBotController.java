package com.example.controller;

import com.example.model.TelegramCreatedBot;
import com.example.request.TelegramCreatedBotRequest;
import com.example.service.TelegramCreatedBotService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/telegram/bots")
@AllArgsConstructor
public class TelegramCreatedBotController {

    private final TelegramCreatedBotService telegramCreatedBotService;

    @PostMapping("/create-bot")
    @Operation(summary = "you have to created bot, botUsername or botToken(required and cannot wrong) by ur own in your Telegram app first before created it")
    public ResponseEntity<?> createBot(@RequestBody TelegramCreatedBotRequest telegramCreatedBotRequest) {
        TelegramCreatedBot telegramCreatedBot = telegramCreatedBotService.createBot(telegramCreatedBotRequest);
        return ResponseEntity.ok().body(telegramCreatedBot);
    }


    @GetMapping("/get-bot-from-database")
    @Operation(summary = "we used only bot has id = 1 in database to send notifications")
    public ResponseEntity<?> getBots() {
        List<TelegramCreatedBot> telegramCreatedBot = telegramCreatedBotService.getBots();
        return ResponseEntity.ok().body(telegramCreatedBot);
    }

    @GetMapping("/get-bot-by-botUsername")
    @Operation(summary = "we used only bot has id = 1 in database to send notifications")
    public ResponseEntity<?> getBotByBotUsername(@RequestParam String botUsername) {
        TelegramCreatedBot telegramCreatedBot = telegramCreatedBotService.getBotByBotUsername(botUsername);
        return ResponseEntity.ok().body(telegramCreatedBot);
    }

    @GetMapping("/get-bot-by-botId/{botId}")
    @Operation(summary = "we used only bot has id = 1 in database to send notifications")
    public ResponseEntity<?> getBotByBotId(@PathVariable Long botId) {
        Optional<TelegramCreatedBot> telegramCreatedBot = telegramCreatedBotService.getBotByBotId(botId);
        return ResponseEntity.ok().body(telegramCreatedBot);
    }

    @PutMapping("/update-bot-by-botUsername")
    @Operation(summary = "you have to updated bot, botUsername or botToken(required and cannot wrong) by ur own in your Telegram app first before updated it")
    public ResponseEntity<?> updateBotByBotUsername(@RequestParam String botUsername, @RequestBody TelegramCreatedBotRequest telegramCreatedBotRequest) {
        TelegramCreatedBot telegramCreatedBot = telegramCreatedBotService.updateBotByBotUsername(botUsername, telegramCreatedBotRequest);
        return ResponseEntity.ok().body(telegramCreatedBot);
    }

    @PutMapping("/update-bot-by-botId{botId}")
    @Operation(summary = "we used only bot has id = 1 in database to send notifications")
    public ResponseEntity<?> updateBotByBotId(@PathVariable Long botId, @RequestBody TelegramCreatedBotRequest telegramCreatedBotRequest) {
        TelegramCreatedBot telegramCreatedBot = telegramCreatedBotService.updateBotByBotId(botId, telegramCreatedBotRequest);
        return ResponseEntity.ok().body(telegramCreatedBot);
    }

    @DeleteMapping("/delete-bot-by-botId/{botId}")
    @Operation(summary = "cannot deleted bot has Id = 1")
    public ResponseEntity<?> deleteBotByBotId(@PathVariable Long botId) {
        telegramCreatedBotService.deleteBotById(botId);
        return ResponseEntity.ok().build();
    }




}
