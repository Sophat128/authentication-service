package com.example.model.request;

import com.example.dto.TransactionHistoryDto;
import com.example.model.respone.TransactionResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
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
