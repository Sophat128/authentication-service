package com.example.repository;

import com.example.entity.FailedMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FailedMessageRepository extends JpaRepository<FailedMessage, Long> {
}
