package com.example.listener;

import com.example.Notification;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaListeners {

    //    @RetryableTopic(
//            attempts = "3",
//            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
//            backoff = @Backoff(delay = 1000, maxDelay = 5_000, random = true),
//            dltTopicSuffix = "-dead-letter"
//    )
    @KafkaListener(
            topics = "notification"
            , groupId = "message-group"
    )
    void listener(ConsumerRecord<String, Notification> notification) {
        log.info("Started consuming message on topic: {}, offset {}, message {}", notification.topic(),
                notification.offset(), notification.value());
        if (notification.value().getNotificationType().equals("EMAIL"))
            throw new IllegalStateException("This is something odd.");
        log.info("Started consuming message on topic: {}, offset {}, message {}", notification.topic(),
                notification.offset(), notification.value());

//        if(notification.offset() % 2 != 0) throw new IllegalStateException("This is something odd.");


    }
}
