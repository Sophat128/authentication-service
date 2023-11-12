package com.example.clienteventservice.service;

import com.example.clienteventservice.domain.model.BankAccount;
import com.example.clienteventservice.domain.model.TransactionHistory;
import com.example.clienteventservice.domain.response.DepositResponse;
import com.example.clienteventservice.domain.response.TransactionResponse;
import com.example.clienteventservice.domain.response.WithdrawResponse;
import com.example.clienteventservice.domain.type.TransactionType;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class DepositService {
    private final BankAccountService bankAccountService;
    private final TransactionService transactionService;
    private final KafkaTemplate<Object, Object> kafkaTemplate;

    public void deposit(String bankAccountNumber, BigDecimal amount) {
        BankAccount toBankAccount = bankAccountService.getBankAccount(bankAccountNumber);
        transactionService.executeDeposit(toBankAccount, amount);
        TransactionResponse depositResponse = new TransactionResponse(bankAccountNumber,amount, TransactionType.DEPOSIT.name());
        Message<TransactionResponse> message = MessageBuilder
                .withPayload(depositResponse)
                .setHeader(KafkaHeaders.TOPIC, "notification")
                .build();
        System.out.println("Message: " + message);
        kafkaTemplate.send(message);
    }


}
