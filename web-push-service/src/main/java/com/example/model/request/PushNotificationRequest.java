package com.example.model.request;

import com.example.model.dto.TransactionHistoryDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PushNotificationRequest {
    private String title;
    private TransactionHistoryDto body;

}
