package com.example.repository;

import com.example.model.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TelegramUserRepository extends JpaRepository<TelegramUser, Long> {

    @Query(nativeQuery = true, value = "SELECT chat_id from telegram_users WHERE user_id = :#{#userId}")
    Long getChatIdByUserId(String userId);

}
