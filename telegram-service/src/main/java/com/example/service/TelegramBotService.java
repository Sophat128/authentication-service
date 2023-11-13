package com.example.service;


import com.example.config.TelegramBotConfig;
import com.example.model.Telegram;
import com.example.repository.TelegramRepository;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;


@Service
public class TelegramBotService extends TelegramLongPollingBot {

    private final TelegramBotConfig telegramBotConfig;
    private final TelegramRepository telegramRepository;



    public TelegramBotService(TelegramBotConfig telegramBotConfig, TelegramRepository userRepository)  {
        this.telegramBotConfig = telegramBotConfig;
        this.telegramRepository = userRepository;
    }

    @Override
    public String getBotUsername() {
        return telegramBotConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return telegramBotConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();

            if ("/start".equals(messageText)) {

                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText("Welcome to your bot! Click the button below to bind your account and navigate to the application.");

                // Include the chatId as a parameter in the URL
                String appUrl = "https://your-app.com?chatId=" + chatId;

                // Create the inline keyboard button
                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                InlineKeyboardButton button = new InlineKeyboardButton("Bind Account");

                // Set the URL to your application's URL with chatId parameter
                button.setUrl(appUrl);

                markup.setKeyboard(List.of(List.of(button)));
                message.setReplyMarkup(markup);




                try {
                    execute(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendTextMessage(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    public Long fetchChatIdByUserId(String userId) {
        Telegram telegram = telegramRepository.findByUserId(userId);
        return telegram.getChatId();
    }


}