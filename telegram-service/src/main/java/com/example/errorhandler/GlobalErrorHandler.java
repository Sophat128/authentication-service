package com.example.errorhandler;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;

public class GlobalErrorHandler implements CommonErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalErrorHandler.class);

    @Override
    public void handleRecord(
            Exception thrownException,
            ConsumerRecord<?, ?> record,
            Consumer<?, ?> consumer,
            MessageListenerContainer container) {
        log.warn("Global error handler for message: {}", record.value().toString());
    }
}
