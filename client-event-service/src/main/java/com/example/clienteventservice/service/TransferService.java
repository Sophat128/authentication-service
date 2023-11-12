package com.example.clienteventservice.service;

import com.example.clienteventservice.domain.model.BankAccount;
import com.example.clienteventservice.domain.response.TransactionResponse;
import com.example.clienteventservice.domain.response.TransferResponse;
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
 * Transfer process management service
 */
@Slf4j
@AllArgsConstructor(onConstructor_ = {@Autowired})
@Service
public class TransferService {

    private TransactionService transactionService;
    private BankAccountService bankAccountService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    //    public void transfer(String fromBankAccountNumber, String toBankAccountNumber, BigDecimal amount) {
//        BankAccount fromBankAccount = bankAccountService.getBankAccount(fromBankAccountNumber);
//        BankAccount toBankAccount = bankAccountService.getBankAccount(toBankAccountNumber);
//        transactionService.executeTransfer(fromBankAccount, toBankAccount, amount);
//        TransactionResponse senderResponse = new TransactionResponse(fromBankAccountNumber, amount,TransactionType.SENDER.name());
//        TransactionResponse receiverResponse = new TransactionResponse(fromBankAccountNumber, amount,TransactionType.RECEIVER.name());
//        Message<TransactionResponse> senderMessage = MessageBuilder
//                .withPayload(senderResponse)
//                .setHeader(KafkaHeaders.TOPIC, "notification")
//                .build();
//        System.out.println("Message: " + senderMessage);
//        kafkaTemplate.send(senderMessage);
//        Message<TransactionResponse> receiverMessage = MessageBuilder
//                .withPayload(receiverResponse)
//                .setHeader(KafkaHeaders.TOPIC, "notification")
//                .build();
//        System.out.println("Message: " + receiverMessage);
//        kafkaTemplate.send(receiverMessage);
//    }
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
    }


}
