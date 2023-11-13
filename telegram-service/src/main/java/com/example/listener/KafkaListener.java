package com.example.listener;

import com.example.model.Telegram;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaListener {
    //    @RetryableTopic(
//            attempts = "3",
//            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
//            backoff = @Backoff(delay = 1000, maxDelay = 5_000, random = true),
//            dltTopicSuffix = "-dead-letter"
//    )
    @org.springframework.kafka.annotation.KafkaListener(
            topics = "telegram"
            , groupId = "message-group"
    )
    void listener(ConsumerRecord<String, Telegram> notification) {
        log.info("Started consuming message on topic: {}, offset {}, message {}", notification.topic(),
                notification.offset(), notification.value());
//        if(notification.offset() % 2 != 0) throw new IllegalStateException("This is something odd.");

    }
}
