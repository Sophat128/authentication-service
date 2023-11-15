package com.example.models.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Data
public class MailScheduleDto {

    private Long scheduleId;

    private UUID userId;

    private String message;

    private String toEmail;

    private LocalDateTime scheduledTime;

    private ZoneId zoneId;

    private Boolean isSended;

}
