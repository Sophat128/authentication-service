package com.example.listener;

import com.example.model.dto.TransactionHistoryDto;
import com.example.model.request.PushNotificationRequest;
import com.example.model.respone.BankAccountResponse;
import com.example.service.WebService;
import com.example.webpush.WebPushService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    public WebConsumer(WebPushService webPushService, WebClient.Builder webClient, WebService webService) {
        this.webPushService = webPushService;
        this.webService = webService;
    }
    @KafkaListener(topics = "${kafka.topics.data}")

    public void sendNotificationToWebPush(ConsumerRecord<String, String> commandsRecord) throws MessagingException, IOException {
        LOGGER.log(Level.INFO, () -> String.format("sendConfirmationEmails() » Topic: %s", commandsRecord.topic()));
        System.out.println("Receive Data: " + commandsRecord.value());

        String trimmedString = commandsRecord.value().replaceAll("^\"|\"$", "");
        String cleanedJson = trimmedString.replaceAll("\\\\", "");


        ObjectMapper objectMapper = new ObjectMapper();

        TransactionHistoryDto transactionHistoryDto = new TransactionHistoryDto();
        try {
            transactionHistoryDto = objectMapper.readValue(cleanedJson, TransactionHistoryDto.class);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }


        PushNotificationRequest pushNotificationRequest = new PushNotificationRequest("Transaction", transactionHistoryDto);
        System.out.println("pushNotificationRequest: " + pushNotificationRequest);
        BankAccountResponse customerInfo = webService.getCustomerInfoByBankAccountNo(transactionHistoryDto.getBankAccountNumber());
        System.out.println("customerInfo: " + customerInfo);
        webPushService.notifySpecificUser(pushNotificationRequest, customerInfo.getCustomerId());
    }
}
