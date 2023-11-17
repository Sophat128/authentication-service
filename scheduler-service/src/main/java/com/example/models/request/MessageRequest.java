package com.example.models.request;

import com.example.models.entity.MailSchedule;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageRequest {

    private String message;

    private LocalDateTime scheduledTime;

    private String zoneId;

    public MailSchedule toMailSchedule() {
        return new MailSchedule(null, this.message, this.scheduledTime.toString(), this.zoneId);
    }
    public MailSchedule toMailSchedule(Long scheduleId) {
        return new MailSchedule(scheduleId, this.message, this.scheduledTime.toString(), this.zoneId);
    }
}