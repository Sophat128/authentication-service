package com.example.config;

import com.example.model.TelegramCreatedBot;
import com.example.repository.TelegramCreatedBotRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class TelegramUserConfig {

    private final TelegramCreatedBotRepository telegramCreatedBotRepository;

    @PostConstruct
    public void addConfig(){
        TelegramCreatedBot telegramCreatedBot = new TelegramCreatedBot();
        telegramCreatedBot.setBotUsername("FintrackAPIBot");
        telegramCreatedBot.setBotToken("6666866418:AAGq-QFHKnhu55REjQv3xv6tfnNDca7xVxA");
        telegramCreatedBot.setBotLink("https://t.me/FintrackAPIBot");
        telegramCreatedBotRepository.save(telegramCreatedBot);
        System.out.println("first running");
    }

    public String botUsername() {
        String botUsername = telegramCreatedBotRepository.findAll().get(0).getBotUsername();
        System.out.println("botUsername: " + botUsername);
        return botUsername;
    }

    public String botToken() {
        String botToken = telegramCreatedBotRepository.findAll().get(0).getBotToken();
        System.out.println("botToken: " + botToken);
        return botToken;
    }



}
