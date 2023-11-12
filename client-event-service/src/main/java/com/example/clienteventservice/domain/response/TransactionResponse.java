package com.example.clienteventservice.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {
    private String accountNumber;
    private BigDecimal amount;
    private String action;
}
