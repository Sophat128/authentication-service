package com.example.config;

import com.example.repository.TelegramCreatedBotRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class TelegramUserConfig {

    private final TelegramCreatedBotRepository telegramCreatedBotRepository;

    public String botUsername() {
        String botUsername = telegramCreatedBotRepository.findById(1L).get().getBotUsername();
        System.out.println("botUsername: " + botUsername);
        return botUsername;
    }

    public String botToken() {
        String botToken = telegramCreatedBotRepository.findById(1L).get().getBotToken();
        System.out.println("botToken: " + botToken);
        return botToken;
    }


}
