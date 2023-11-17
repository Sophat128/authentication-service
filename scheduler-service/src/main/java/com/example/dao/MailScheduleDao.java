package com.example.dao;

import com.example.models.entity.MailSchedule;
import com.example.models.request.Request;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Component
public interface MailScheduleDao {

    String createSchedule(Request request, ZonedDateTime zonedDateTime);

    List<MailSchedule> getAllScheduleByUserId(String userId, Integer pageNo, Integer pageSize);

    List<MailSchedule> getSchedules(int page, int size);

    Long updateSchedule(Request request, ZonedDateTime zonedDateTime, Long scheduleId);

    void deleteSchedule(Long id);

    MailSchedule getScheduleById(Long id);

    Boolean getScheduleByIdAndIsSendedFalse(Long id);

    void deleteMailSchedule(Long scheduleId);

    MailSchedule getScheduleByUserId(String userId);

    void deleteJobAndTrigger(Long scheduleId);

    JobDetail getJobDetail(String scheduleId, Request request);

    Trigger getSimpleTrigger(JobDetail jobDetail, ZonedDateTime zonedDateTime);

    void scheduleJob(JobDetail jobDetail, Trigger trigger);

    void validateLocalDateTimeWithZoneId(LocalDateTime localDateTime, String zoneId);

}
