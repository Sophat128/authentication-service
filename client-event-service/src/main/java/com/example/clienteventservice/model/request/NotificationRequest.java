package com.example.clienteventservice.model.request;

import com.example.clienteventservice.model.entity.NotificationType;
import lombok.Data;


@Data
public class NotificationRequest {
    private NotificationType notificationType;
}
