package com.example.listener;

import com.example.models.Email;
import com.example.services.EmailSenderService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jakarta.mail.MessagingException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailConsumer {
    private static final Logger LOGGER = LogManager.getLogger(EmailConsumer.class);
    private final EmailSenderService emailSenderService;

    @Autowired
    public EmailConsumer(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }

    @KafkaListener(topics = "${kafka.topics.data}")
    public void sendConfirmationEmails(ConsumerRecord<?, ?> commandsRecord) throws MessagingException, IOException {
        LOGGER.log(Level.INFO, () -> String.format("sendConfirmationEmails() » Topic: %s", commandsRecord.topic()));
        JsonElement object = new Gson().fromJson(commandsRecord.value().toString(), JsonObject.class);
        System.out.println("Receiving message: " + object);
        var subject = object.getAsJsonObject().get("subject").getAsString();
        System.out.println("Subject work");

        var to = object.getAsJsonObject().get("to").getAsString();
        System.out.println("to work");

        Map<String, Object> props = new HashMap<>();
        props.put("name", "Sophat");
        props.put("subscriptionDate", new Date());
        Email email = Email.builder()
                .withTo(to)
                .withFrom("From Spring <support@jorgel.io>")
                .withContent("---")
                .withSubject(subject)
                .withProps(props)
                .build();

//        if (to.equals("sophattann128@gmail.com"))
//            throw new IllegalStateException("This is something odd.");
        System.out.println("Convert to Email: " + email);
        emailSenderService.sendConfirmationEmail(email, null, null, null);
        LOGGER.log(Level.INFO, () -> " »» Mail sent successfully");
    }
}
