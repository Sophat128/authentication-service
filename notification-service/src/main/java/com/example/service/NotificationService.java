package com.example.service;

import com.example.Notification;
import com.example.entities.request.EmailRequest;
import com.example.entities.request.NotificationRequest;
import org.springframework.web.multipart.MultipartFile;

public interface NotificationService {
    void publishToMessageBroker(NotificationRequest notification);
    Notification addNotificationData(NotificationRequest notificationRequest);

    void publishToMail(EmailRequest emailRequest);
    void sendData(String data);
}
