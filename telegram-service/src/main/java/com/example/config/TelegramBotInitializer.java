package com.example.config;

import com.example.service.TelegramBotUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class TelegramBotInitializer {

    private final TelegramBotUserService telegramBotUserService;
    private final Logger logger = LoggerFactory.getLogger(TelegramBotInitializer.class);

    public TelegramBotInitializer(TelegramBotUserService telegramBotUserService) {
        this.telegramBotUserService = telegramBotUserService;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void init() {
        try {
            telegramBotUserService.clearWebhook();

            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(telegramBotUserService);
            logger.info("Telegram bot registered successfully.");
        } catch (TelegramApiException e) {
            logger.error("Error registering Telegram bot", e);
        }
    }

}
