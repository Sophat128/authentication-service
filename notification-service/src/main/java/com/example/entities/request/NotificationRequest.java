package com.example.entities.request;

import com.example.Notification;
import com.example.entities.NotificationType;
import lombok.Data;

import java.util.UUID;
@Data
public class NotificationRequest {

    private NotificationType notificationType;
    private String subject;
    private String content;
    private String status;
    private UUID userId;
    private Integer retryCount;

    public Notification toEntity(){
        return new Notification(notificationType.name(),subject,content,status,userId,retryCount);
    }


}