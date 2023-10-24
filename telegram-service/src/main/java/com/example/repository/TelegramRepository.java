package com.example.repository;

import com.example.model.Telegram;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TelegramRepository extends JpaRepository<Telegram, Long> {
    Telegram findUserByChatId(Long id);
    Telegram findByUserId(UUID id);
}
