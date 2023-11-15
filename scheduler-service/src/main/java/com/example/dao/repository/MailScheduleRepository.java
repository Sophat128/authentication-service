package com.example.dao.repository;

import com.example.models.entity.MailSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MailScheduleRepository extends JpaRepository<MailSchedule, Long> {
    Page<MailSchedule> findAllByUserId(String userId, Pageable pageable);
    Optional<MailSchedule> findByUserId(String userId);
}
