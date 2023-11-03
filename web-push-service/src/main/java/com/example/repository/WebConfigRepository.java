package com.example.repository;

import com.example.model.entities.WebDataConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WebConfigRepository extends JpaRepository<WebDataConfig, UUID> {

}
