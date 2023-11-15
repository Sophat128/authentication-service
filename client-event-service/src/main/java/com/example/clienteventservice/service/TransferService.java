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

    private final KafkaTemplate<String, TransactionHistoryDto> kafkaTemplate;

    //    public void transfer(String fromBankAccountNumber, String toBankAccountNumber, BigDecimal amount) {
//        BankAccount fromBankAccount = bankAccountService.getBankAccount(fromBankAccountNumber);
//        BankAccount toBankAccount = bankAccountService.getBankAccount(toBankAccountNumber);
//
//        transactionService.executeTransfer(fromBankAccount, toBankAccount, amount);
//        TransactionHistory senderResponse = transactionService
//                .getTransactionHistoryBuilder(
//                        TransactionType.TRANSFER,
//                        StatementType.EXPENSE,
//                        fromBankAccount,
//                        amount
//                ).build();
//
//        TransactionHistory receiverResponse = transactionService
//                .getTransactionHistoryBuilder(
//                        TransactionType.TRANSFER,
//                        StatementType.INCOME,
//                        toBankAccount,
//                        amount
//                ).build();
//
//        Message<TransactionHistoryDto> senderMessage = MessageBuilder
//                .withPayload(senderResponse.toDto())
//                .setHeader(KafkaHeaders.TOPIC, "notification")
//                .build();
//        System.out.println("Message from bank account: " + senderMessage);
//        kafkaTemplate.send(senderMessage);
//
//        Message<TransactionHistoryDto> receiverMessage = MessageBuilder
//                .withPayload(receiverResponse.toDto())
//                .setHeader(KafkaHeaders.TOPIC, "notification")
//                .build();
//        System.out.println("Message from bank account: " + receiverMessage);
//        kafkaTemplate.send(receiverMessage);
//
//    }
    public void transfer(String fromBankAccountNumber, String toBankAccountNumber, BigDecimal amount) {
        BankAccount fromBankAccount = bankAccountService.getBankAccount(fromBankAccountNumber);
        BankAccount toBankAccount = bankAccountService.getBankAccount(toBankAccountNumber);

        transactionService.executeTransfer(fromBankAccount, toBankAccount, amount);

        sendTransferNotification(TransactionType.SENDER, StatementType.EXPENSE, fromBankAccount, amount, toBankAccount);
        sendTransferNotification(TransactionType.RECEIVER, StatementType.INCOME, toBankAccount, amount, fromBankAccount);
    }

    private void sendTransferNotification(TransactionType transactionType, StatementType statementType, BankAccount bankAccountUser1, BigDecimal amount, BankAccount bankAccountUser2) {
        TransactionHistory transactionHistory = transactionService.getTransactionHistoryBuilder(
                transactionType,
                statementType,
                bankAccountUser1,
                amount
        ).build();
        if (transactionType.equals(TransactionType.SENDER)) {
            transactionHistory.setReceivedAccountNumber(bankAccountUser2.getAccountNumber());
        } else if (transactionType.equals(TransactionType.RECEIVER)) {
            transactionHistory.setReceivedAccountNumber(bankAccountUser2.getAccountNumber());
        }

        Message<TransactionHistoryDto> message = MessageBuilder
                .withPayload(transactionHistory.toDto())
                .setHeader(KafkaHeaders.TOPIC, "notification-service")
                .build();

        System.out.println("Message from bank account: " + message);
        kafkaTemplate.send(message);
    }


}
