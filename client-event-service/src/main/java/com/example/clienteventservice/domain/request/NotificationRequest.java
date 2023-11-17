package com.example.clienteventservice.domain.request;

import com.example.type.NotificationType;
import lombok.Data;


@Data
public class NotificationRequest {
    private NotificationType notificationType;
}
