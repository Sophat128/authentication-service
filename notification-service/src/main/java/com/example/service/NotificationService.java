package com.example.service;

import com.example.Notification;
import com.example.entities.request.EmailRequest;
import com.example.entities.request.NotificationRequest;
import org.springframework.kafka.support.SendResult;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface NotificationService {
    void publishToMessageBroker(NotificationRequest notification);
    Notification addNotificationData(NotificationRequest notificationRequest);

    void publishToMail(EmailRequest emailRequest);
}
