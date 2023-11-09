package com.example.clienteventservice.service;

import com.example.clienteventservice.domain.model.Subscription;

import java.util.List;

public interface SubscriptionService {
    List<Subscription> getNotificationTypeByUserId(String userId);
}
