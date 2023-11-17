package com.example.listener;

import com.example.dto.ScheduleDto;
import com.example.dto.TransactionHistoryDto;
import com.example.model.request.PushNotificationRequest;
import com.example.model.respone.BankAccountResponse;
import com.example.service.WebService;
import com.example.webpush.WebPushService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

@Service
public class WebConsumer {
    private static final Logger LOGGER = LogManager.getLogger(WebConsumer.class);
    private final WebPushService webPushService;
    private final WebService webService;
    private String WEB_SCHEDULE = "web-notification-schedule";

    public WebConsumer(WebPushService webPushService, WebClient.Builder webClient, WebService webService) {
        this.webPushService = webPushService;
        this.webService = webService;
    }
    @KafkaListener(topics = "${kafka.topics.data}")

    public void sendNotificationToWebPush(ConsumerRecord<String, TransactionHistoryDto> commandsRecord) throws MessagingException, IOException {
        LOGGER.log(Level.INFO, () -> String.format("sendConfirmationEmails() » Topic: %s", commandsRecord.topic()));
        System.out.println("Receive Data: " + commandsRecord.value());

        PushNotificationRequest pushNotificationRequest = new PushNotificationRequest("Transaction", commandsRecord.value());
        System.out.println("pushNotificationRequest: " + pushNotificationRequest);

        BankAccountResponse customerInfo = webService.getCustomerInfoByBankAccountNo(commandsRecord.value().getBankAccountNumber());
        System.out.println("customerInfo: " + customerInfo);
//
        webPushService.notifySpecificUser(pushNotificationRequest, customerInfo.getCustomerId());
    }

    @KafkaListener(topics = "${kafka.topics.schedule}")

    public void webPushSchedule(ConsumerRecord<String, ScheduleDto> commandsRecord) throws MessagingException, IOException {
        LOGGER.log(Level.INFO, () -> String.format("sendConfirmationEmails() » Topic: %s", commandsRecord.topic()));
        System.out.println("Receive Data: " + commandsRecord.value());
        System.out.println("pushNotificationRequest: " + commandsRecord.value());
        webPushService.notifySpecificUserWithSchedule(commandsRecord.value());
    }
}
