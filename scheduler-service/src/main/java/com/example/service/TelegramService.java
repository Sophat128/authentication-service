package com.example.service;

import com.example.exception.DownStreamServerException;
import com.example.response.ApiResponse;
import org.springframework.stereotype.Component;

@Component
public interface TelegramService {
    ApiResponse<?> sendMessage(String message, String jwt);
    void get();
}
