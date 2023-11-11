package com.example.service.Imp;

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
    public String transfer(String amount,String email) {
        Message<String> message = MessageBuilder
                .withPayload(amount)
//                .setHeader(KafkaHeaders.TOPIC, "notification")
                .setHeader(KafkaHeaders.TOPIC,"send.email.kb")
                .build();
        System.out.println("Message: " + message);
        kafkaTemplate.send(message);


        return amount;
    }

}
