package com.example.clienteventservice.service;

import com.example.clienteventservice.domain.model.BankAccount;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Transfer process management service
 */
@Slf4j
@AllArgsConstructor(onConstructor_ = {@Autowired})
@Service
public class TransferService {

    private TransactionService transactionService;
    private BankAccountService bankAccountService;

    public void transfer(String fromBankAccountNumber, String toBankAccountNumber, BigDecimal amount) {
        BankAccount fromBankAccount = bankAccountService.getBankAccount(fromBankAccountNumber);
        BankAccount toBankAccount = bankAccountService.getBankAccount(toBankAccountNumber);

        transactionService.executeTransfer(fromBankAccount, toBankAccount, amount);

        sendTransactionNotification(fromBankAccountNumber, amount, TransactionType.SENDER);
        sendTransactionNotification(fromBankAccountNumber, amount, TransactionType.RECEIVER);
    }

    private void sendTransactionNotification(String accountNumber, BigDecimal amount, TransactionType transactionType) {
        TransactionResponse response = new TransactionResponse(accountNumber, amount, transactionType.name());
        Message<TransactionResponse> message = MessageBuilder
                .withPayload(response)
                .setHeader(KafkaHeaders.TOPIC, "notification")
                .build();
        kafkaTemplate.send(message);

        TransactionHistory transactionHistory = transactionService
                .getTransactionHistoryTransfer(
                        TransactionType.TRANSFER,
                        StatementType.EXPENSE,
                        fromBankAccount,
                        toBankAccountNumber,
                        amount
                ).build();

        Message<TransactionHistoryDto> message = MessageBuilder
                .withPayload(transactionHistory.toDto())
                .setHeader(KafkaHeaders.TOPIC, "notification")
                .build();
        System.out.println("Message from bank account: " + message);
        kafkaTemplate.send(message);
    }


}
