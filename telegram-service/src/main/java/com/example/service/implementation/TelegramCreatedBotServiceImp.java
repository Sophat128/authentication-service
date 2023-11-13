package com.example.service.implementation;

import com.example.model.TelegramCreatedBot;
import com.example.repository.TelegramCreatedBotRepository;
import com.example.request.TelegramCreatedBotRequest;
import com.example.service.TelegramCreatedBotService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TelegramCreatedBotServiceImp implements TelegramCreatedBotService {

    private final TelegramCreatedBotRepository telegramCreatedBotRepository;

    @Override
    public TelegramCreatedBot createBot(TelegramCreatedBotRequest telegramCreatedBotRequest) {
        TelegramCreatedBot telegramCreatedBot = new TelegramCreatedBot();
        telegramCreatedBot.setBotUsername(telegramCreatedBotRequest.getBotUsername());
        telegramCreatedBot.setBotToken(telegramCreatedBotRequest.getBotToken());
        return telegramCreatedBotRepository.save(telegramCreatedBot);
    }

    @Override
    public List<TelegramCreatedBot> getBots() {
        List<TelegramCreatedBot> telegramCreatedBots = telegramCreatedBotRepository.findAll();
        return telegramCreatedBots;
    }


    @Override
    public TelegramCreatedBot getBotByBotUsername(String botUsername) {
        TelegramCreatedBot telegramCreatedBot = telegramCreatedBotRepository.getBotByBotUsername(botUsername);
        return telegramCreatedBot;
    }

    @Override
    public Optional<TelegramCreatedBot> getBotByBotId(Long botId) {
        Optional<TelegramCreatedBot> telegramCreatedBot = telegramCreatedBotRepository.findById(botId);
        return telegramCreatedBot;
    }

    @Override
    public TelegramCreatedBot updateBotByBotUsername(String botUsername, TelegramCreatedBotRequest telegramCreatedBotRequest) {
        TelegramCreatedBot bot = telegramCreatedBotRepository.getBotByBotUsername(botUsername);
        bot.setBotUsername(telegramCreatedBotRequest.getBotUsername());
        bot.setBotToken(telegramCreatedBotRequest.getBotToken());
        return telegramCreatedBotRepository.save(bot);
    }

    @Override
    public TelegramCreatedBot updateBotByBotId(Long botId, TelegramCreatedBotRequest telegramCreatedBotRequest) {
        Optional<TelegramCreatedBot> telegramCreatedBot = telegramCreatedBotRepository.findById(botId);
        telegramCreatedBot.get().setBotUsername(telegramCreatedBotRequest.getBotUsername());
        telegramCreatedBot.get().setBotToken(telegramCreatedBotRequest.getBotToken());
        return telegramCreatedBotRepository.save(telegramCreatedBot.get());
    }

    @Override
    public void deleteBotById(Long botId) {
        if (botId.equals(1L)) {
            throw new IllegalArgumentException("Bot with ID 1 cannot be deleted.");
        } else {
            telegramCreatedBotRepository.deleteById(botId);
        }
    }


}
