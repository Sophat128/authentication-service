package com.example.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import javax.management.Notification;
import java.util.UUID;

@Repository
public interface NewsRepository extends JpaRepository<UUID, Notification> {
    Mono<Boolean> saveNews(String date, Object newsObject) throws JsonProcessingException;
}
