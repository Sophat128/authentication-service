package com.example.listener;

import com.example.model.dto.ScheduleDto;
import com.example.model.dto.TransactionHistoryDto;
import com.example.model.entities.BankAccount;
import com.example.model.entities.WebPushHistory;
import com.example.model.request.PushNotificationRequest;
import com.example.model.respone.ApiResponse;
import com.example.model.respone.BankAccountResponse;
import com.example.model.type.NotificationType;
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
import java.util.HashMap;
import java.util.Map;

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
//        ApiResponse<BankAccount> customerInfo = webService.getCustomerInfoByBankAccountNo(transactionHistoryDto.getBankAccountNumber());
//        try{
//        System.out.println("customerInfo: " + customerInfo);
//            System.out.println("customerInfo id: " + customerInfo.getPayload().getCustomerId());
//
//        }catch (Exception e){
//            System.out.println("Error; " + e.getMessage());
//        }

        WebPushHistory webPushHistory = new WebPushHistory();
        webPushHistory.setNotificationType(NotificationType.EMAIL.name());
        webPushHistory.setMessageName(transactionHistoryDto.getType().name());



    webPushService.saveWebHistory(webPushHistory);

        webPushService.notifySpecificUser(pushNotificationRequest, transactionHistoryDto.getCustomerId().toString());
    }

    @KafkaListener(topics = "${kafka.topics.schedule}")

    public void webPushSchedule(ConsumerRecord<String, String> commandsRecord) throws MessagingException, IOException {
        LOGGER.log(Level.INFO, () -> String.format("sendConfirmationEmails() » Topic: %s", commandsRecord.topic()));
        System.out.println("Receive Data: " + commandsRecord.value());
        System.out.println("pushNotificationRequest: " + commandsRecord.value());
        ScheduleDto scheduleDto = parseScheduleDto(commandsRecord.value());
        System.out.println("Converted data: " + scheduleDto.getUserId());
        System.out.println("Converted data field: " + scheduleDto.getMessage());
        if(scheduleDto.getUserId().equals("null")){
            System.out.println("Send to all user");
            webPushService.notifyAll(scheduleDto);
        }
        webPushService.notifySpecificUserWithSchedule(scheduleDto);
    }

    private ScheduleDto parseScheduleDto(String input) {
        String userId = null;
        String message = null;

        String[] keyValuePairs = input.substring(input.indexOf("(") + 1, input.indexOf(")")).split(",\\s*");

        for (String pair : keyValuePairs) {
            String[] keyValue = pair.split("=");

            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();

                if ("userId".equals(key)) {
                    userId = value;
                } else if ("message".equals(key)) {
                    message = value;
                }
            }
        }

        if (userId != null && message != null) {
            // Assuming userId is a valid UUID string
            return new ScheduleDto(userId, message);
        } else {
            return null;
        }
    }



}
