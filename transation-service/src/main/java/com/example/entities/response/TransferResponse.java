package com.example.entities.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferResponse {
    private String from;
    private String to;
    private Double amount;
    private String message;
}
