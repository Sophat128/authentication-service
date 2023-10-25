package com.example.service;


import com.example.config.TelegramBotConfig;
import com.example.exception.NotFoundException;
import com.example.model.Telegram;
import com.example.repository.TelegramRepository;
import com.example.response.ApiResponse;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.security.Principal;
import java.util.UUID;


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
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            if (messageText.equals("/start")) {
                registerUser(update.getMessage());
                startCommandReceived(chatId, update.getMessage().getChat().getLastName(), update.getMessage().getChat().getFirstName());
            } else {
                prepareAndSendMessage(chatId, "Sorry, command was not recognized!");
            }
        }

    }

    private UUID userId =null;
    public void getCurrentId(Principal principal) {
        userId = UUID.fromString(principal.getName());
    }

    private void registerUser(Message message) {

        Long chatId = message.getChatId();
        var chat = message.getChat();

        Telegram user;
        user = new Telegram();

        user.setFirstName(chat.getFirstName());
        user.setLastName(chat.getLastName());
        user.setUserName(chat.getUserName());
        user.setChatId(chatId);
        user.setUserId(userId);

        if (telegramRepository.findUserByChatId(message.getChatId()) == null) {
            if(userId!=null){
                telegramRepository.save(user);
            }
        }
    }

    private void startCommandReceived(Long chatId, String firstName,String lastname) {
        String answer="";
        if(userId!=null){
            answer = EmojiParser.parseToUnicode("Hi, " + lastname+" "+firstName + "\nThank you for register our service ");
        }
        else
            answer= EmojiParser.parseToUnicode("Hi, " + lastname+" "+firstName + "\nWhat can i help you");

        sendMessage(chatId, answer);
    }

    public void sendMessage(Long chatId, String textToSend) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public ApiResponse<?> sendMessage(String message, Principal principal){
        if(principal.getName()==null){
            throw new NotFoundException("use need to login");
        }
        Telegram telegram=telegramRepository.findByUserId(UUID.fromString(principal.getName()));
        if(telegram!=null){
            String answer= EmojiParser.parseToUnicode("Hi, " + telegram.getLastName()+" "+telegram.getFirstName() + "\n"+message);
            sendMessage(telegram.getChatId(),answer);
        }
        return ApiResponse.builder()
                .message("send message success")
                .status(200)
                .build();
    }

    private void prepareAndSendMessage(Long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

}
