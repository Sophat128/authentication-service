package com.example.config;

import com.example.model.TelegramCreatedBot;
import com.example.repository.TelegramCreatedBotRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
@AllArgsConstructor
public class TelegramUserConfig {

    private final TelegramCreatedBotRepository telegramCreatedBotRepository;

    @PostConstruct
    public void addConfig() {
        String botUsername = "FintrackAPIBot";
        String botToken = "6666866418:AAGq-QFHKnhu55REjQv3xv6tfnNDca7xVxA";
        String botLink = "https://t.me/FintrackAPIBot";

        if (!existsByBotUsernameAndBotTokenAndBotLink(botUsername, botToken, botLink)) {
            TelegramCreatedBot telegramCreatedBot = new TelegramCreatedBot();
            telegramCreatedBot.setBotUsername(botUsername);
            telegramCreatedBot.setBotToken(botToken);
            telegramCreatedBot.setBotLink(botLink);
            telegramCreatedBotRepository.save(telegramCreatedBot);
            System.out.println("First running");
        } else {
            System.out.println("Bot configuration already exists. Skipping...");
        }
    }

    private boolean existsByBotUsernameAndBotTokenAndBotLink(String botUsername, String botToken, String botLink) {
        Optional<TelegramCreatedBot> existingBot = telegramCreatedBotRepository
                .findByBotUsernameAndBotTokenAndBotLink(botUsername, botToken, botLink);
        return existingBot.isPresent();
    }

    public String botUsername() {
        String botUsername = telegramCreatedBotRepository.findAll().get(0).getBotUsername();
        return botUsername;
    }

    public String botToken() {
        String botToken = telegramCreatedBotRepository.findAll().get(0).getBotToken();
        return botToken;
    }



}
