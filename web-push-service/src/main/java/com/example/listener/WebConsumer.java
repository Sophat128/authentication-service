package com.example.listener;

import com.example.model.request.PushNotificationRequest;
import com.example.model.respone.BankAccountResponse;
import com.example.model.respone.TransactionResponse;
import com.example.service.WebService;
import com.example.webpush.WebPushService;
import com.fasterxml.jackson.databind.JsonNode;
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
//    public void sendConfirmationEmails(ConsumerRecord<String, TransactionResponse> commandsRecord) throws MessagingException, IOException {
//        LOGGER.log(Level.INFO, () -> String.format("sendConfirmationEmails() » Topic: %s", commandsRecord.topic()));
//        System.out.println("Receive Data: " + commandsRecord.value());
//
//        PushNotificationRequest pushNotificationRequest = new PushNotificationRequest("Transaction", commandsRecord.value().toString());
//        System.out.println("pushNotificationRequest: " + pushNotificationRequest);
//
//        webPushService.notifySpecificUser(pushNotificationRequest, "4ca43b72-3f19-4e70-be4b-e18bf498d451");
//        System.out.println("Receive Data after: " + commandsRecord.value());
//
//    }
    public void sendConfirmationEmails(ConsumerRecord<Object, String> commandsRecord) throws MessagingException, IOException {
        LOGGER.log(Level.INFO, () -> String.format("sendConfirmationEmails() » Topic: %s", commandsRecord.topic()));
        System.out.println("Receive Data: " + commandsRecord.value());

        // Create an ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        String data = commandsRecord.value();
        String trimmedString = data.replaceAll("^\"|\"$", "");
        String cleanedString = trimmedString.replaceAll("\\\\", "");
        // Parse the JSON string into a JsonNode object
        JsonNode root = objectMapper.readTree(cleanedString);
        System.out.println("root " + root);
        String accountNumber =  root.get("bankAccountNumber").asText();

        PushNotificationRequest pushNotificationRequest = new PushNotificationRequest("Transaction", commandsRecord.value());

        BankAccountResponse customerInfo = webService.getCustomerInfoByBankAccountNo(accountNumber);
        System.out.println("customerInfo: " + customerInfo);

        webPushService.notifySpecificUser(pushNotificationRequest, customerInfo.getCustomerId());
    }
}
