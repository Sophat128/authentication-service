package com.example.service.Imp;

import com.example.entities.request.TransferRequest;
import com.example.entities.response.TransferResponse;
import com.example.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TransactionServiceImp implements TransactionService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public TransferResponse transfer(TransferRequest transferRequest) {

        Message<TransferResponse> message = MessageBuilder
                .withPayload(transferRequest.toEntity())
                .setHeader(KafkaHeaders.TOPIC, "notification")
                .build();
        System.out.println("Message: " + message);
        kafkaTemplate.send(message);

        return message.getPayload();
    }

}
