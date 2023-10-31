package com.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Entity
@Table(name = "telegram")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Telegram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long chatId;
    private UUID userId;
    private Boolean isSubscribe;

    public Telegram(Long chatId, UUID userId, Boolean isSubscribe) {
        this.chatId = chatId;
        this.userId = userId;
        this.isSubscribe = isSubscribe;
    }
}
