package com.example.repository;

import com.example.model.TelegramCreatedBot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TelegramCreatedBotRepository extends JpaRepository<TelegramCreatedBot, Long> {
    TelegramCreatedBot getBotByBotUsername(String botName);

}
