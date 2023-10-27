package org.example.repository;

import org.example.entity.Smtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SmtpRepository extends JpaRepository<Smtp,UUID>{
}
