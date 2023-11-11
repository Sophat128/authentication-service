package org.example.listener;
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

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class EmailConsumer {
    private static final Logger LOGGER = LogManager.getLogger(EmailConsumer.class);
    private final EmailKbService emailKbService;

    public EmailConsumer(EmailKbService emailKbService) {
        this.emailKbService = emailKbService;
    }

    @KafkaListener(topics = "${kafka.topics.data}")
    public void sendConfirmationEmails(ConsumerRecord<?, ?> commandsRecord) throws MessagingException, IOException {
        LOGGER.log(Level.INFO, () -> String.format("sendConfirmationEmails() » Topic: %s", commandsRecord.topic()));
        JsonElement object = new Gson().fromJson(commandsRecord.value().toString(), JsonObject.class);
        System.out.println("Receiving message: " + object);
        var subject = object.getAsJsonObject().get("subject").getAsString();
        String from = object.getAsJsonObject().get("from").getAsString();
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
                .withFrom(from.trim( ))
                .withContent(content)
                .withSubject(subject)
                .withProps(props)
                .build();
        if (object.getAsJsonObject().has("attachmentFilePath")) {
            String attachmentFilePath = object.getAsJsonObject().get("attachmentFilePath").getAsString();
            File file = new File(attachmentFilePath);
            System.out.println("File Exists: " + file.exists());
            email.setAttachmentFilePath(attachmentFilePath);
        }

        System.out.println("Convert to Email: " + email);
        emailKbService.sendConfirmationEmail(email);
        LOGGER.log(Level.INFO, () -> " »» Mail sent successfully");
    }
}