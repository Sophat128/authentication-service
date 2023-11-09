package com.example.service.impl;

import com.example.Email;
import com.example.entities.request.EmailRequest;
import com.example.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final WebClient.Builder webClient;
    @Override
    public void publishToMail(EmailRequest emailRequest) {
        Email email = emailRequest.toEntity();
        Message<Email> message = MessageBuilder
                .withPayload(email)
                .setHeader(KafkaHeaders.TOPIC, "send.email")
                .build();
        System.out.println("Message: " + message);
        kafkaTemplate.send(message);

    }
    @Override
    public Object getPublicKey(){
        return webClient.baseUrl("http://api-gateway-service/api/v1/webpush/publicKey")
                .build()
                .get()
                .retrieve().bodyToMono(Object.class).block();
    }
    @Override
    public void pushToWeb(Object data) {
        Message<Object> message = MessageBuilder
                .withPayload(data)
                .setHeader(KafkaHeaders.TOPIC, "web-notification")
                .build();
        System.out.println("Message: " + message);
        kafkaTemplate.send(message);
    }
}
