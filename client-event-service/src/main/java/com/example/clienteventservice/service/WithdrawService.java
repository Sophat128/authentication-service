package com.example.clienteventservice.service;

import com.example.clienteventservice.domain.dto.BankAccountDto;
import com.example.clienteventservice.domain.model.BankAccount;
import com.example.clienteventservice.domain.response.TransactionResponse;
import com.example.clienteventservice.domain.response.WithdrawResponse;
import com.example.clienteventservice.domain.type.TransactionType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Withdraw process management service
 */
@Slf4j
@AllArgsConstructor(onConstructor_ = {@Autowired})
@Service
public class WithdrawService {

    private TransactionService transactionService;
    private BankAccountService bankAccountService;
    private final KafkaTemplate<Object, Object> kafkaTemplate;

    public void withdraw(String bankAccountNumber, BigDecimal amount) {
        BankAccount bankAccount = bankAccountService.getBankAccount(bankAccountNumber);
        transactionService.executeWithdraw(bankAccount, amount);
        TransactionResponse withdrawResponse = new TransactionResponse(bankAccountNumber,amount, TransactionType.WITHDRAW.name());
        Message<TransactionResponse> message = MessageBuilder
                .withPayload(withdrawResponse)
                .setHeader(KafkaHeaders.TOPIC, "notification")
                .build();
        System.out.println("Message: " + message);
        kafkaTemplate.send(message);

    }

}
