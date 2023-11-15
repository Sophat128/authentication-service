package com.example.clienteventservice.service;

import com.example.clienteventservice.domain.model.BankAccount;
import com.example.clienteventservice.domain.model.TransactionHistory;
import com.example.dto.TransactionHistoryDto;
import com.example.type.StatementType;
import com.example.type.TransactionType;
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
 * Transfer process management service
 */
@Slf4j
@AllArgsConstructor(onConstructor_ = {@Autowired})
@Service
public class TransferService {

    private TransactionService transactionService;
    private BankAccountService bankAccountService;
    private final KafkaTemplate<Object, Object> kafkaTemplate;

    public void transfer(String fromBankAccountNumber, String toBankAccountNumber, BigDecimal amount) {
        BankAccount fromBankAccount = bankAccountService.getBankAccount(fromBankAccountNumber);
        BankAccount toBankAccount = bankAccountService.getBankAccount(toBankAccountNumber);

        transactionService.executeTransfer(fromBankAccount, toBankAccount, amount);

        TransactionHistory senderResponse = transactionService
                .getTransactionHistoryBuilder(
                        TransactionType.SENDER,
                        StatementType.EXPENSE,
                        fromBankAccount,
                        amount
                ).build();

        TransactionHistory receiverResponse = transactionService
                .getTransactionHistoryBuilder(
                        TransactionType.RECEIVER,
                        StatementType.INCOME,
                        toBankAccount,
                        amount
                ).build();

        senderResponse.setReceivedAccountNumber(toBankAccountNumber);
        receiverResponse.setReceivedAccountNumber(fromBankAccountNumber);

        sendTransactionNotification(senderResponse);
        sendTransactionNotification(receiverResponse);

//        Message<TransactionHistoryDto> message = MessageBuilder
//                .withPayload(transactionHistory.toDto())
//                .setHeader(KafkaHeaders.TOPIC, "notification")
//                .build();
//        System.out.println("Message from bank account: " + message);
//        kafkaTemplate.send(message);
    }

    private void sendTransactionNotification(TransactionHistory history) {
        Message<TransactionHistoryDto> message = MessageBuilder
                .withPayload(history.toDto())
                .setHeader(KafkaHeaders.TOPIC, "notification")
                .build();
        kafkaTemplate.send(message);

    }


}
