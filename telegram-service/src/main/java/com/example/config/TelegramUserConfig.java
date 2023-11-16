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
        String botToken = "6666866418:AAEG2U_O6xBUyqr5UkF3iAdEfx5QuV_HGuI";
        String botLink = "https://t.me/FintrackAPIBot";

        if (!existsByBotUsernameAndBotTokenAndBotLink(botUsername, botToken, botLink)) {
            try {
            TelegramCreatedBot telegramCreatedBot = new TelegramCreatedBot();
            telegramCreatedBot.setBotUsername(botUsername);
            telegramCreatedBot.setBotToken(botToken);
            telegramCreatedBot.setBotLink(botLink);
            telegramCreatedBotRepository.save(telegramCreatedBot);
            System.out.println("First running");

            }catch (Exception e){
                System.out.println("Error: " + e.getMessage());
            }
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
