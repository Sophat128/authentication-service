package org.example.listener;

import com.example.dto.ScheduleDto;
import com.example.dto.UserDtoClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jakarta.mail.MessagingException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Email;
import org.example.service.EmailKbService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class EmailConsumer {
    private static final Logger LOGGER = LogManager.getLogger(EmailConsumer.class);
    private final EmailKbService emailKbService;
    private final WebClient webClient;
    private static final String EMAIL_TOPIC_SCHEDULE = "send.email.kb.schedule";


    public EmailConsumer(EmailKbService emailKbService, WebClient webClient) {
        this.emailKbService = emailKbService;
        this.webClient = webClient;
    }

    public UserDtoClient getCustomerId(UUID customerId) {
        return webClient.get()
                .uri("http://localhost:8088/api/v1/customers/{customerId}", customerId)
                .retrieve()
                .bodyToMono(UserDtoClient.class)
                .block();
    }


    @KafkaListener(topics = "${kafka.topics.data}")
    public void sendConfirmationEmails(ConsumerRecord<Object, String> commandsRecord) throws MessagingException, IOException {
        LOGGER.log(Level.INFO, () -> String.format("sendConfirmationEmails() » Topic: %s", commandsRecord.topic()));

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode object = objectMapper.readTree(commandsRecord.value());

        System.out.println("Receiving message: " + object);

        UUID customerId = UUID.fromString(object.get("customerId").asText());
        UserDtoClient userDtoClient = getCustomerId(customerId);
        System.out.println(userDtoClient.getEmail());

        List<String> recipients = Collections.singletonList(userDtoClient.getEmail());

        String subject = object.get("type").asText() + "   Confirmation";
        String from = "KB Prasac Bank";
        from = from.replaceAll("\\s", "");
        String content;

        if ("SENDER".equals(object.get("type").asText())) {
            content = String.format("Dear %s,\n\n" +
                    "You have transfer  $%.2f to  account number " + object.get("receivedAccountNumber").asText() +
                    " Thank you for using our services.\n\n" +
                    "Sincerely,\n KB Prasac Bank", userDtoClient.getUsername(), object.get("amount").asDouble(), object.get("bankAccountNumber").asText());
        } else if ("RECEIVER".equals(object.get("type").asText())) {
            content = String.format("Dear %s,\n\n" +
                    "You have received  $%.2f from account number " + object.get("receivedAccountNumber").asText() +
                    " Thank you for using our services.\n\n" +
                    "Sincerely,\n KB Prasac Bank", userDtoClient.getUsername(), object.get("amount").asDouble(), object.get("bankAccountNumber").asText());
        } else {
            content = String.format("Dear %s,\n\n" +
                    "You have received a " + object.get("type").asText().toLowerCase() + " of $%.2f to your account (%s).\n\n" +
                    "Thank you for choosing our services.\n\n" +
                    "Sincerely,\n KB Prasac Bank", userDtoClient.getUsername(), object.get("amount").asDouble(), object.get("bankAccountNumber").asText());
        }

        // Add other recipients if available
        JsonNode toList = object.get("to");
        if (toList != null && toList.isArray()) {
            for (JsonNode element : toList) {
                recipients.add(element.asText());
            }
        }

        Map<String, Object> props = new HashMap<>();
        props.put("name", userDtoClient.getUsername());
        props.put("subscriptionDate", new Date());
        System.out.println(recipients);
        Email email = Email.builder()
                .withTo(recipients)
                .withFrom(from)
                .withContent(content)
                .withSubject(subject)
                .withProps(props)
                .build();

        // Check if the attachmentFilePath is present and not empty in the JSON object
        if (object.has("attachmentFilePath")) {
            String attachmentFilePath = object.get("attachmentFilePath").asText();

            // Check if attachmentFilePath is not empty
            if (!attachmentFilePath.isEmpty()) {
                try {
                    File file = new File(attachmentFilePath);
                    if (file.exists() && file.isFile()) {
                        System.out.println("File Exists: " + file.exists());
                        email.setAttachmentFilePath(attachmentFilePath);
                    } else {
                        System.out.println("Error: File does not exist or is not a regular file.");
                    }
                } catch (Exception e) {
                    System.out.println("Error checking file: " + e.getMessage());
                }
            }
        }

        System.out.println("Convert to Email: " + email);
        emailKbService.sendConfirmationEmail(email);
        LOGGER.log(Level.INFO, () -> " »» Mail sent successfully");
    }

    @KafkaListener(topics = "send.email")
    public void sendConfirmationEmailss(ConsumerRecord<?, ?> commandsRecord) throws MessagingException, IOException {
        LOGGER.log(Level.INFO, () -> String.format("sendConfirmationEmails() » Topic: %s", commandsRecord.topic()));
        JsonElement object = new Gson().fromJson(commandsRecord.value().toString(), JsonObject.class);
        System.out.println("Receiving message: " + object);
        var subject = object.getAsJsonObject().get("subject").getAsString();
        String from = object.getAsJsonObject().get("from").getAsString();
        from = from.replaceAll("\\s", "");
        String content = object.getAsJsonObject().get("content").getAsString();
        JsonArray toList = object.getAsJsonObject().getAsJsonArray("to");
        List<String> recipients = new ArrayList<>();
        for (JsonElement element : toList) {
            recipients.add(element.getAsString());
        }

        Map<String, Object> props = new HashMap<>();
        props.put("name", "Sophat");
        props.put("subscriptionDate", new Date());
        Email email = Email.builder()
                .withTo(recipients)
                .withFrom(from.trim())
                .withContent(content)
                .withSubject(subject)
                .withProps(props)
                .build();

        // Check if the attachmentFilePath is present and not empty in the JSON object
        if (object.getAsJsonObject().has("attachmentFilePath")) {
            String attachmentFilePath = object.getAsJsonObject().get("attachmentFilePath").getAsString();

            // Check if attachmentFilePath is not empty
            if (!attachmentFilePath.isEmpty()) {
                try {
                    File file = new File(attachmentFilePath);
                    if (file.exists() && file.isFile()) {
                        System.out.println("File Exists: " + file.exists());
                        email.setAttachmentFilePath(attachmentFilePath);
                    } else {
                        System.out.println("Error: File does not exist or is not a regular file.");
                    }
                } catch (Exception e) {
                    System.out.println("Error checking file: " + e.getMessage());
                }
            }
        }

        System.out.println("Convert to Email: " + email);
        emailKbService.sendConfirmationEmail(email);
        LOGGER.log(Level.INFO, () -> " »» Mail sent successfully");
    }

    @KafkaListener(
            topics = EMAIL_TOPIC_SCHEDULE,
            groupId = "notification-consumer"
    )
    public void sendConfirmationEmailSchedule(ConsumerRecord<String, String> commandsRecord) throws MessagingException, IOException {
        LOGGER.log(Level.INFO, () -> String.format("sendConfirmationEmails() » Topic: %s", commandsRecord.value()));

        ScheduleDto scheduleDto = parseScheduleDto(commandsRecord.value());

        UUID customerId = UUID.fromString(scheduleDto.getUserId());
        UserDtoClient userDtoClient = getCustomerId(customerId);
        System.out.println(userDtoClient.getEmail());

        List<String> recipients = Collections.singletonList(userDtoClient.getEmail());

        String subject = "KB Prasac Bank Information";
        String from = "KB Prasac Bank";
        from = from.replaceAll("\\s", "");
        Map<String, Object> props = new HashMap<>();
        props.put("name", userDtoClient.getUsername());
        props.put("subscriptionDate", new Date());
        String content = String.format("Dear %s,\n\n" +
                scheduleDto.getMessage(), userDtoClient.getUsername());
        System.out.println(recipients);
        Email email = Email.builder()
                .withTo(recipients)
                .withFrom(from)
                .withContent(content)
                .withSubject(subject)
                .withProps(props)
                .build();
        System.out.println("Convert to Email schedule: " + email);
        emailKbService.sendConfirmationEmail(email);
        LOGGER.log(Level.INFO, () -> " »» Mail sent successfully");
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