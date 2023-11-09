package com.example.clienteventservice.service;

import com.example.clienteventservice.domain.dto.BankAccountDto;
import com.example.clienteventservice.domain.model.BankAccount;
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
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void withdraw(String bankAccountNumber, BigDecimal amount) {
        BankAccountDto bankAccountDto = new BankAccountDto(bankAccountNumber,amount);
        BankAccount bankAccount = bankAccountService.getBankAccount(bankAccountNumber);
        transactionService.executeWithdraw(bankAccount, amount);
        Message<BankAccountDto> message = MessageBuilder
                .withPayload(bankAccountDto)
                .setHeader(KafkaHeaders.TOPIC, "notification")
                .build();
        System.out.println("Message: " + message);
        kafkaTemplate.send(message);

    }

}
