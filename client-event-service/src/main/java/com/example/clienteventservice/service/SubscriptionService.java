package com.example.clienteventservice.service;

import com.example.clienteventservice.model.entity.Subscription;
import com.example.dto.SubscriptionDto;

import java.util.List;

public interface SubscriptionService {
    List<Subscription> getNotificationTypeByUserId(String userId);
}
