package com.example.listener;

import com.example.config.WebClientConfig;
import com.example.dto.TransactionHistoryDto;
import com.example.response.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class NotificationConsumer {

    private final KafkaTemplate<String, TransactionHistoryDto> kafkaTemplate;
    private final KafkaTemplate<String, String> kafkaTemplateSchedule;

    private final WebClientConfig webClientConfig;

    private static final String NOTIFICATION_TOPIC = "notification-service";
    private static final String NOTIFICATION_TOPIC_SCHEDULE = "notification-service-schedule";
    private static final String WEB_TOPIC_SCHEDULE = "web-service-schedule";


    private static final String TELEGRAM_TOPIC = "telegram";
    private static final String EMAIL_TOPIC = "send.email.kb";
    private static final String WEB_TOPIC = "web-notification-service";
    private static final Logger LOGGER = LogManager.getLogger(NotificationConsumer.class);



    public NotificationConsumer(KafkaTemplate<String, TransactionHistoryDto> kafkaTemplate, KafkaTemplate<String, String> kafkaTemplateSchedule, WebClientConfig webClientConfig) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTemplateSchedule = kafkaTemplateSchedule;
        this.webClientConfig = webClientConfig;
    }

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
                .setHeader(KafkaHeaders.TOPIC, WEB_TOPIC)
                .build();
        System.out.println("Message: " + message);
        kafkaTemplate.send(message);



        String userId = String.valueOf(notification.value().getCustomerId());
        Message<TransactionHistoryDto> messages = MessageBuilder
                .withPayload(notification.value())
                .setHeader(KafkaHeaders.TOPIC, EMAIL_TOPIC)
                .build();
        System.out.println("Message: " + messages);
        kafkaTemplate.send(messages);

        String subscriptionUrl = "http://client-event-service/api/v1/clients/get-notification";
        WebClient web = webClientConfig.webClientBuilder().baseUrl(subscriptionUrl).build();

        ApiResponse<List<Map<String, Object>>> subscriptionDtos = web.get()
                .uri("/{userId}", userId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<Map<String, Object>>>>() {})
                .block();

        assert subscriptionDtos != null;
        List<Map<String, Object>> payload = subscriptionDtos.getPayload();
        List<String> notificationTypes = payload.stream()
                .map(subscription -> (String) subscription.get("notificationType"))
                .toList();

        System.out.println("Notification: " + notificationTypes);

        for (String type : notificationTypes) {
            System.out.println("Type: " + type);
            log.info("Processing notificationType: {}", type);
            if (type.equals("TELEGRAM")) {
                kafkaTemplate.send(TELEGRAM_TOPIC, notification.key(), notification.value());
                log.info("Sent message to TELEGRAM_TOPIC: {}", notification.value());
//            } else if (type.equals("EMAIL")) {
//                kafkaTemplate.send(EMAIL_TOPIC, notification.key(), notification.value());
//                log.info("Sent message to EMAIL_TOPIC: {}", notification.value());
            } else {
                log.info("this userId doesn't have subscribe telegram or email notification types!");
            }
        }

    }

    @KafkaListener(
            topics = NOTIFICATION_TOPIC_SCHEDULE,
            groupId = "notification-consumer"
    )

    public void webPushSchedule(ConsumerRecord<String, String> commandsRecord) throws MessagingException, IOException {

        LOGGER.log(Level.INFO, () -> String.format("sendConfirmationEmails() » Topic: %s", commandsRecord.topic()));
        System.out.println("Receive Data: " + commandsRecord.value());
        System.out.println("pushNotificationRequest: " + commandsRecord.value());
        Message<String> messages = MessageBuilder
                .withPayload(commandsRecord.value())
                .setHeader(KafkaHeaders.TOPIC, WEB_TOPIC_SCHEDULE)
                .build();
        System.out.println("Message: " + messages);
        kafkaTemplateSchedule.send(messages);
//        webPushService.notifySpecificUserWithSchedule(commandsRecord.value());
    }


}
