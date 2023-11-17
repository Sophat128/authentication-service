package com.example.mapper;

import com.example.models.dto.MailScheduleDto;
import com.example.models.entity.MailSchedule;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;


public class ScheduleMapper {
    public static MailScheduleDto toDto(MailSchedule mailSchedule) {
        MailScheduleDto response = new MailScheduleDto();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        response.setScheduleId(mailSchedule.getScheduleId());
        response.setUserId(UUID.fromString(mailSchedule.getUserId()));
        response.setMessage(mailSchedule.getMessage());
        response.setScheduledTime(LocalDateTime.parse(mailSchedule.getScheduleDateTime(), formatter));
        response.setZoneId(ZoneId.of(mailSchedule.getScheduleZoneId()));
        response.setIsSended(mailSchedule.isSended());
        return response;
    }
    public static MailScheduleDto messageDto(MailSchedule mailSchedule) {
        MailScheduleDto response = new MailScheduleDto();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        response.setScheduleId(mailSchedule.getScheduleId());
        response.setMessage(mailSchedule.getMessage());
        response.setScheduledTime(LocalDateTime.parse(mailSchedule.getScheduleDateTime(), formatter));
        response.setZoneId(ZoneId.of(mailSchedule.getScheduleZoneId()));
        response.setIsSended(mailSchedule.isSended());
        return response;
    }
}
