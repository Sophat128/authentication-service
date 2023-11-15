package com.example.listener;

import com.example.config.WebClientConfig;
import com.example.dto.TransactionHistoryDto;
import com.example.response.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j
public class NotificationConsumer {

    private final KafkaTemplate<String, TransactionHistoryDto> kafkaTemplate;
    private final WebClientConfig webClientConfig;

    private static final String NOTIFICATION_TOPIC = "notification";
    private static final String TELEGRAM_TOPIC = "telegram";
    private static final String EMAIL_TOPIC = "email";


    public NotificationConsumer(KafkaTemplate<String, TransactionHistoryDto> kafkaTemplate, WebClientConfig webClientConfig) {
        this.kafkaTemplate = kafkaTemplate;
        this.webClientConfig = webClientConfig;
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
    void listener(ConsumerRecord<String, TransactionHistoryDto> notification) throws JsonProcessingException {
        log.info("Started consuming message on topic: {}, offset {}, message {}", notification.topic(),
                notification.offset(), notification.value());


//        ObjectMapper objectMapper = new ObjectMapper();
//        String stringValue = String.valueOf(notification.value());
//        TransactionHistoryDto transactionHistoryDto = objectMapper.readValue(stringValue, TransactionHistoryDto.class);
//
//        log.info("Started consuming message on topic: {}, offset {}, message {}", notification.topic(),
//                notification.offset(), transactionHistoryDto);

        Message<TransactionHistoryDto> message = MessageBuilder
                .withPayload(notification.value())
                .setHeader(KafkaHeaders.TOPIC, "web")
                .build();
        System.out.println("Message: " + message);
        kafkaTemplate.send(message);

        String userId = String.valueOf(notification.value().getCustomerId());

        String subscriptionUrl = "http://client-event-service/api/v1/clients/get-notification";
        WebClient web = webClientConfig.webClientBuilder().baseUrl(subscriptionUrl).build();

        ApiResponse<List<Map<String, Object>>> subscriptionDtos = web.get()
                .uri("/{userId}", userId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<Map<String, Object>>>>() {})
                .block();

        List<Map<String, Object>> payload = subscriptionDtos.getPayload();
        List<String> notificationTypes = payload.stream()
                .map(subscription -> (String) subscription.get("notificationType"))
                .collect(Collectors.toList());

        System.out.println("Notification: " + notificationTypes);

        for (String type : notificationTypes) {
            String notificationType = type;
            System.out.println("Type: " + type);
            log.info("Processing notificationType: {}", notificationType);
            if (notificationType.equals("TELEGRAM")) {
                kafkaTemplate.send(TELEGRAM_TOPIC, notification.key(), notification.value());
                log.info("Sent message to TELEGRAM_TOPIC: {}", notification.value());
            } else if (notificationType.equals("EMAIL")) {
                kafkaTemplate.send(EMAIL_TOPIC, notification.key(), notification.value());
                log.info("Sent message to EMAIL_TOPIC: {}", notification.value());
            } else {
                log.info("this userId doesn't have subscribe telegram or email notification types!");
            }
        }

    }


}
