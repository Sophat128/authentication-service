package com.example.listener;

import com.example.dto.TransactionHistoryDto;
import com.example.repository.TelegramUserRepository;
import com.example.service.TelegramBotUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class TelegramConsumer {

    private static final String TELEGRAM_TOPIC = "telegram";

    private final TelegramUserRepository telegramUserRepository;
    private final TelegramBotUserService telegramBotUserService;

    public TelegramConsumer(TelegramUserRepository telegramUserRepository, TelegramBotUserService telegramBotUserService) {
        this.telegramUserRepository = telegramUserRepository;
        this.telegramBotUserService = telegramBotUserService;
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
    void listener(ConsumerRecord<String, TransactionHistoryDto> telegram) {
        log.info("Started consuming message on topic: {}, offset {}, message {}", telegram.topic(),
                telegram.offset(), telegram.value());

        try {
            UUID userId = telegram.value().getCustomerId();

//            Check if the userId exists in your telegram_users table and get chatId
            Long chatId = telegramUserRepository.getChatIdByUserId(String.valueOf(userId));
            System.out.println("chatId: " + chatId);

            telegramBotUserService.sendTextMessage(chatId, telegram.value());
        } catch (Exception e) {
            log.error("Exception while processing Kafka record", e);
        }

    }


}
