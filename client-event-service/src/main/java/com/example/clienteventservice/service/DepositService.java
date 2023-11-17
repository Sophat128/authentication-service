package com.example.clienteventservice.service;

import com.example.clienteventservice.domain.model.BankAccount;
import com.example.clienteventservice.domain.model.TransactionHistory;
import com.example.dto.TransactionHistoryDto;
import com.example.type.StatementType;
import com.example.type.TransactionType;
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

    private final KafkaTemplate<String, TransactionHistoryDto> kafkaTemplate;

    public void deposit(String toBankAccountNumber, BigDecimal amount) {
        BankAccount toBankAccount = bankAccountService.getBankAccount(toBankAccountNumber);
        transactionService.executeDeposit(toBankAccount, amount);

        TransactionHistory transactionHistory = transactionService
                .getTransactionHistoryBuilder(
                        TransactionType.DEPOSIT,
                        StatementType.INCOME,
                        toBankAccount,
                        amount
                ).build();

        Message<TransactionHistoryDto> message = MessageBuilder
                .withPayload(transactionHistory.toDto())
                .setHeader(KafkaHeaders.TOPIC, "notification-service")
                .build();
        System.out.println("Message: " + message);
        kafkaTemplate.send(message);
    }


}