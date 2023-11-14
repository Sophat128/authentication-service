package org.example.listener;
import com.example.dto.TransactionHistoryDto;
import com.example.dto.UserDto;
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

        // Use Jackson's ObjectMapper to parse the JSON string into a JsonNode
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode object = objectMapper.readTree(commandsRecord.value());

        System.out.println("Receiving message: " + object);

        UUID customerId = UUID.fromString(object.get("customerId").asText());
        UserDtoClient userDtoClient = getCustomerId(customerId);

        // Set the "to" field directly from userDtoClient.getEmail()
        List<String> recipients = Collections.singletonList(userDtoClient.getEmail());

        String subject = object.get("type").asText()+"   Confirmation";
        String from = "KB Prasac Bank";
        from = from.replaceAll("\\s", "");
        String content = String.format("Dear %s,\n\n" +
                "We have received a " + object.get("type").asText().toLowerCase()+ " of $%.2f to your account (%s).\n\n" +
                "Thank you for choosing our services.\n\n" +
                "Sincerely,\nYour Bank", userDtoClient.getUsername(), object.get("amount").asDouble(), object.get("bankAccountNumber").asText());

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


}