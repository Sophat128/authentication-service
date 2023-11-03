package com.example.listener;

import com.example.model.request.PushNotificationRequest;
import com.example.webpush.WebPushService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class WebConsumer {
    private static final Logger LOGGER = LogManager.getLogger(WebConsumer.class);
    private final WebPushService webPushService;
    public WebConsumer(WebPushService webPushService) {
        this.webPushService = webPushService;
    }
    @KafkaListener(topics = "${kafka.topics.data}")
    public void sendConfirmationEmails(ConsumerRecord<?, ?> commandsRecord) throws MessagingException, IOException {
        LOGGER.log(Level.INFO, () -> String.format("sendConfirmationEmails() » Topic: %s", commandsRecord.topic()));
        PushNotificationRequest pushNotificationRequest = new PushNotificationRequest("Transaction", commandsRecord.value().toString());
        webPushService.notifySpecificUser(pushNotificationRequest);
    }
}
