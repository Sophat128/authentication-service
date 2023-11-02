package com.example.listener;

import com.example.config.BeanConfig;
import com.example.service.TelegramBotService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TelegramConsumer {

    private static final String TELEGRAM_TOPIC = "telegram";

    private final TelegramBotService telegramBotService;

    private final BeanConfig beanConfig;

    public TelegramConsumer(TelegramBotService telegramBotService, BeanConfig beanConfig) {
        this.telegramBotService = telegramBotService;
        this.beanConfig = beanConfig;
    }

    //    @RetryableTopic(
//            attempts = "3",
//            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
//            backoff = @Backoff(delay = 1000, maxDelay = 5_000, random = true),
//            dltTopicSuffix = "-dead-letter"
//    )
    @KafkaListener(
            topics = TELEGRAM_TOPIC,
            groupId = "notification-consumer"
    )
    void listener(ConsumerRecord<String, String> telegram) {
        log.info("Started consuming message on topic: {}, offset {}, message {}", telegram.topic(),
                telegram.offset(), telegram.value());

        String _sender = "user id: ";
        String _action = " has send money.";
        String messageValue = telegram.value();

        int senderIndex = messageValue.indexOf(_sender);
        int actionIndex = messageValue.indexOf(_action);

        if (senderIndex >= 0 && actionIndex >= 0) {
            // Extract the user ID from the messageValue
            String userId = messageValue.substring(senderIndex + _sender.length(), actionIndex);
            System.out.println("Userid: " + userId);

            // Check if the userId exists in your Telegram service's table
            Long chatId = telegramBotService.fetchChatIdByUserId(userId);
                telegramBotService.sendTextMessage(chatId, messageValue);
        }
    }

}
