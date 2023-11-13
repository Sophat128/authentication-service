package com.example.service;

import com.example.model.TelegramCreatedBot;
import com.example.request.TelegramCreatedBotRequest;

import java.util.List;
import java.util.Optional;

public interface TelegramCreatedBotService {
    TelegramCreatedBot createBot(TelegramCreatedBotRequest telegramCreatedBotRequest);
    TelegramCreatedBot getBotByBotUsername(String botUsername);

    Optional<TelegramCreatedBot> getBotByBotId(Long botId);

    TelegramCreatedBot updateBotByBotUsername(String botUsername, TelegramCreatedBotRequest telegramCreatedBotRequest);

    TelegramCreatedBot updateBotByBotId(Long botId, TelegramCreatedBotRequest telegramCreatedBotRequest);

    List<TelegramCreatedBot> getBots();

    void deleteBotById(Long botId);
}