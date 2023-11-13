package com.example.service;


import com.example.config.TelegramUserConfig;
import com.example.config.WebClientConfig;
import com.example.dto.TransactionHistoryDto;
import com.example.dto.UserDtoClient;
import com.example.type.StatementType;
import com.example.type.TransactionType;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.DeleteWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


@Service
public class TelegramBotUserService extends TelegramLongPollingBot {

    private final TelegramUserConfig telegramUserConfig;
    private final WebClientConfig webClientConfig;

    public TelegramBotUserService(TelegramUserConfig telegramUserConfig, WebClientConfig webClientConfig)  {
        this.telegramUserConfig = telegramUserConfig;
        this.webClientConfig = webClientConfig;
    }


    @Override
    public String getBotUsername() {
        return telegramUserConfig.botUsername();
    }

    @Override
    public String getBotToken() {
        return telegramUserConfig.botToken();
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();

            if ("/start".equals(messageText)) {

                String notificationEmoji = EmojiParser.parseToUnicode(":bell:");
                String emojiHeart = EmojiParser.parseToUnicode(":heart:");
                String botEmoji = EmojiParser.parseToUnicode(":robot_face:");

                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText("Welcome to KB Prasac Bank's bot " + emojiHeart + "\n" +
                        "Click the button below to bind your account and navigate to the application" +
                        " for get notifications " + notificationEmoji + " of your transactions from this bot " + botEmoji);

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

    public void clearWebhook() {
        try {
            DeleteWebhook deleteWebhook = new DeleteWebhook();
            execute(deleteWebhook);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    public void sendTextMessage(Long chatId, TransactionHistoryDto transactionHistoryDto) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        System.out.println("type: " + transactionHistoryDto.getType());

        String fromAccountNumber = transactionHistoryDto.getBankAccountNumber();
        UUID userId = transactionHistoryDto.getCustomerId();

        String getCustomerByIdUrl = "http://localhost:8088/api/v1/customers";
        WebClient getCustomerById = webClientConfig.webClientBuilder().baseUrl(getCustomerByIdUrl).build();

        UserDtoClient userDtoClient = getCustomerById.get()
                .uri("/{userId}", userId)
                .retrieve()
                .bodyToMono(UserDtoClient.class)
                .block();

        String firstName = userDtoClient.getFirstName();
        String lastName = userDtoClient.getLastName();

        String emojiCheckmark = EmojiParser.parseToUnicode(":white_check_mark:");
        String emojiThanks = EmojiParser.parseToUnicode(":pray:");
        String emojiHeart = EmojiParser.parseToUnicode(":heart:");
        String emojiKey = EmojiParser.parseToUnicode(":key:");
        String emojiDollar = "\uD83D\uDCB5";

        if (transactionHistoryDto.getType().equals(TransactionType.WITHDRAW)) {
            BigDecimal amount = transactionHistoryDto.getAmount();

            String messageToUser = "Hello " + firstName + " " + lastName + " " + emojiHeart + "\n" +
                    "Thank you for using our KB Prasac Bank Services " + emojiThanks + "\n\n" +
                    "We confirm your transaction has completed " + emojiCheckmark + "\n\n" +
                    "You have WITHDRAWN $" + amount + " " + emojiDollar + "\n" +
                    "From this account number " + fromAccountNumber + " " + emojiKey + "\n";

            sendMessage.setText(messageToUser);
        }

        if(transactionHistoryDto.getType().equals(TransactionType.TRANSFER)) {
            if(transactionHistoryDto.getStatementType().equals(StatementType.EXPENSE)) {
                String toAcccountNumber = transactionHistoryDto.getReceivedAccountNumber();
                BigDecimal amount = transactionHistoryDto.getAmount();

                String messageToUser = "Hello " + firstName + " " + lastName + " " + emojiHeart + "\n" +
                        "Thank you for using our KB Prasac Bank Services " + emojiThanks + "\n\n" +
                        "We confirm your transaction has completed " + emojiCheckmark + "\n\n" +
                        "You have TRANSFERRED $" + amount + " " + emojiDollar + "\n" +
                        "From this account number " + fromAccountNumber + " " + emojiKey + "\n" +
                        "To this account number " + toAcccountNumber + " " + emojiKey;

                sendMessage.setText(messageToUser);
            }

        }

        if(transactionHistoryDto.getType().equals(TransactionType.DEPOSIT)) {
            BigDecimal amount = transactionHistoryDto.getAmount();

            String messageToUser = "Hello " + firstName + " " + lastName + " " + emojiHeart + "\n" +
                    "Thank you for using our KB Prasac Bank Services " + emojiThanks + "\n\n" +
                    "We confirm your transaction has completed " + emojiCheckmark + "\n\n" +
                    "You have DEPOSITED $" + amount + " " + emojiDollar + "\n" +
                    "To this account number " + fromAccountNumber + " " + emojiKey;

            sendMessage.setText(messageToUser);
        }

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


}
