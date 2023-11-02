package com.example.listener;

import com.example.config.BeanConfig;
import com.example.dto.SubscriptionDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;


@Service
@Slf4j
public class NotificationConsumer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final BeanConfig beanConfig;

    private static final String NOTIFICATION_TOPIC = "notification";
    private static final String TELEGRAM_TOPIC = "telegram";
    private static final String EMAIL_TOPIC = "email";


    public NotificationConsumer(KafkaTemplate<String, String> kafkaTemplate, BeanConfig beanConfig) {
        this.kafkaTemplate = kafkaTemplate;
        this.beanConfig = beanConfig;
    }

    //    @RetryableTopic(
//            attempts = "3",
//            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
//            backoff = @Backoff(delay = 1000, maxDelay = 5_000, random = true),
//            dltTopicSuffix = "-dead-letter"
//    )

    @KafkaListener(
            topics = NOTIFICATION_TOPIC,
            groupId = "notification-consumer"
    )
    void listener(ConsumerRecord<String, String> notification) {
        log.info("Started consuming message on topic: {}, offset {}, message {}", notification.topic(),
                notification.offset(), notification.value());

        String _sender = "user id: ";
        String _action = " has send money.";
        String messageValue = notification.value();

        int senderIndex = messageValue.indexOf(_sender);
        int actionIndex = messageValue.indexOf(_action);

        if (senderIndex >= 0 && actionIndex >= 0) {
            // Extract the user ID from the messageValue
            String userId = messageValue.substring(senderIndex + _sender.length(), actionIndex);
            System.out.println("Userid: " + userId);

            String subscriptionUrl = "http://localhost:8088/api/v1/clients/get-notification/" + userId;
            WebClient web = beanConfig.webClientBuilder().baseUrl(subscriptionUrl).build();

            List<SubscriptionDto> subscriptionDtos = web.get()
                    .uri(subscriptionUrl)
                    .retrieve()
                    .bodyToFlux(SubscriptionDto.class)
                    .collectList()
                    .block();

            System.out.println("check subscriptions: " + subscriptionDtos);

            for (SubscriptionDto subscriptionDto : subscriptionDtos) {
                String notificationType = subscriptionDto.getNotificationType();
                log.info("Processing notificationType: {}", notificationType);

                if ("null".equals(notificationType)) {
                    // Forward the data to the TELEGRAM_TOPIC for TELEGRAM notifications
                    kafkaTemplate.send(TELEGRAM_TOPIC, notification.key(), messageValue);
                    log.info("Sent message to TELEGRAM_TOPIC: {}", messageValue);
                } else if ("EMAIL".equals(notificationType)) {
                    kafkaTemplate.send(EMAIL_TOPIC, notification.key(), messageValue);
                    log.info("Sent message to EMAIL_TOPIC: {}", messageValue);
                }
            }
        }

    }


}
