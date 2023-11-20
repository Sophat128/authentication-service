package com.example.repository;

import com.example.model.entities.WebPushHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebPushHistoryRepository extends JpaRepository<WebPushHistory, Long> {
}
