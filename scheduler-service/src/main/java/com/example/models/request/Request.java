package com.example.models.request;

import com.example.models.entity.MailSchedule;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;
import lombok.ToString;

//import javax.persistence.Temporal;
//import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Request {

    private UUID userId;

    private String message;

    private LocalDateTime scheduledTime;

    private String zoneId;

    public Request(String message, LocalDateTime scheduledTime, String zoneId) {
        this.message = message;
        this.scheduledTime = scheduledTime;
        this.zoneId = zoneId;
    }

    public MailSchedule toMailSchedule() {
        return new MailSchedule(null, String.valueOf(this.userId), this.message, this.scheduledTime.toString(), this.zoneId.toString());
    }

    public MailSchedule toMailSchedule(Long scheduleId) {
        return new MailSchedule(scheduleId, String.valueOf(this.userId), this.message, this.scheduledTime.toString(), this.zoneId.toString());
    }


}
