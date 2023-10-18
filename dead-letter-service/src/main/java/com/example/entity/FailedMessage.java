package com.example.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class FailedMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;
    private String topic;
    private Long consumerOffset;
    private String exception;
    public FailedMessage() {}
    public FailedMessage(String message) {
        this.message = message;
    }

    public FailedMessage(String message, String topic, Long consumerOffset, String exception) {
        this.message = message;
        this.exception = exception;
        this.consumerOffset = consumerOffset;
        this.topic = topic;
    }
}
