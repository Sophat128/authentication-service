package com.example.dao.impl;

import com.example.dao.MailScheduleDao;
import com.example.dao.repository.MailScheduleRepository;
import com.example.exception.BadRequestException;
import com.example.exception.InternalServerException;
import com.example.exception.NotFoundException;
import com.example.jobs.MailScheduleJob;
import com.example.models.entity.MailSchedule;
import com.example.models.request.Request;

import jakarta.transaction.Transactional;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

//import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;

import static com.example.utils.Constants.MailScheduleJob.*;
import static com.example.utils.Constants.QuartzScheduler.*;

@Component
public class MailScheduleDaoImpl implements MailScheduleDao {

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private MailScheduleRepository mailScheduleRepository;

    @Override
    @Transactional
    public String createSchedule(Request request, ZonedDateTime zonedDateTime) {
        String scheduleId = saveSchedule(request);
        JobDetail jobDetail = getJobDetail(scheduleId, request);
        Trigger simpleTrigger = getSimpleTrigger(jobDetail, zonedDateTime);
        scheduleJob(jobDetail, simpleTrigger);
        System.out.println("createSchedule");
        return scheduleId;
    }

    public String saveSchedule(Request request) {
        try {
            System.out.println("saveSchedule: " + request);
            MailSchedule save = mailScheduleRepository.save(request.toMailSchedule());
            return save.getScheduleId().toString();
        } catch (Exception e) {
            throw new InternalServerException("Unable to save schedule to DB");
        }
    }

    public JobDetail getJobDetail(String scheduleId, Request request) {
        System.out.println("getJobDetail");
        Integer jobId = Integer.valueOf(scheduleId);
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(TO_MAIL, request.getUserId());
        jobDataMap.put(USERID, String.valueOf(request.getUserId()));
        jobDataMap.put(MESSAGE, request.getMessage());
        jobDataMap.put(SCHEDULE_ID, scheduleId);

        return JobBuilder.newJob(MailScheduleJob.class)
                .withIdentity(String.valueOf(jobId), JOB_DETAIL_GROUP_ID)
                .withDescription(JOB_DETAIL_DESCRIPTION)
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    public Trigger getSimpleTrigger(JobDetail jobDetail, ZonedDateTime zonedDateTime) {
        System.out.println("getSimpleTrigger");
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), TRIGGER_GROUP_ID)
                .withDescription(TRIGGER_DESCRIPTION)
                .startAt(Date.from(zonedDateTime.toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }

    public Trigger getCronTrigger(JobDetail jobDetail, String cronExpression) {
        System.out.println("getCronTrigger");
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), TRIGGER_GROUP_ID)
                .withDescription(TRIGGER_DESCRIPTION)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression).
                        withMisfireHandlingInstructionFireAndProceed().inTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC)))
                .build();
    }

    public void scheduleJob(JobDetail jobDetail, Trigger trigger) {
        try {
            System.out.println("scheduleJob");
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException schedulerException) {
            throw new InternalServerException("Error creating the schedule");
        }
    }

    @Override
    public void validateLocalDateTimeWithZoneId(LocalDateTime localDateTime, String zoneId) {
        ZonedDateTime inputZonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.of(zoneId));
        ZonedDateTime currentZonedDateTime = ZonedDateTime.now(ZoneId.of(zoneId));

        LocalDateTime inputLocalDateTime = inputZonedDateTime.toLocalDateTime();
        LocalDateTime currentLocalDateTime = currentZonedDateTime.toLocalDateTime();

        if (inputZonedDateTime.isBefore(currentZonedDateTime)) {
            throw new BadRequestException("Can not input time in the past.");
        } else {
            if (inputLocalDateTime.getMinute() == currentLocalDateTime.getMinute()) {
                throw new BadRequestException("Can not input in the current time.");
            }
        }
    }

    @Override
    public List<MailSchedule> getAllScheduleByUserId(String userId, Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        Page<MailSchedule> mailSchedules = mailScheduleRepository.findAllByUserId(userId, pageable);
        if (!mailSchedules.isEmpty()) {
            return mailSchedules.getContent();
        } else
            throw new NotFoundException("user don't have schedule sended");
//        return response;

    }

    @Override
    public List<MailSchedule> getSchedules(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<MailSchedule> schedules = mailScheduleRepository.findAll(pageable);
        return schedules.getContent();
    }

    @Override
    @Transactional
    public void deleteSchedule(Long scheduleId) {
        deleteMailSchedule(scheduleId);
        deleteJobAndTrigger(scheduleId);
        mailScheduleRepository.deleteById(scheduleId);
    }

    public void deleteMailSchedule(Long scheduleId) {
        try {
//            System.out.println("deleteMailSchedule");
            Optional<MailSchedule> optional = mailScheduleRepository.findById(scheduleId);
            if (optional.isPresent()) {
                MailSchedule mailSchedule = optional.get();
                mailSchedule.setSended(true);
                mailScheduleRepository.save(mailSchedule);
            }
        } catch (Exception e) {
            throw new InternalServerException("Error deleting schedule");
        }
    }

    @Override
    public void deleteJobAndTrigger(Long scheduleId) {
        try {
            scheduler.unscheduleJob(new TriggerKey(scheduleId.toString(), TRIGGER_GROUP_ID));
            scheduler.deleteJob(new JobKey(scheduleId.toString(), JOB_DETAIL_GROUP_ID));
        } catch (SchedulerException schedulerException) {
            throw new InternalServerException("Unable to delete the job from scheduler");
        }
    }

    @Override
    public MailSchedule getScheduleById(Long id) {
        try {
            return mailScheduleRepository.findById(id).get();
        } catch (Exception e) {
            throw new NotFoundException("schedule id " + id + " is not found");
        }
    }

    @Override
    public Boolean getScheduleByIdAndIsSendedFalse(Long scheduleId) {
        MailSchedule mailSchedule = getScheduleById(scheduleId);
        if (mailSchedule.isSended())
            return false;
        return true;
    }

    @Override
    public MailSchedule getScheduleByUserId(String userId) {
        try {
            return mailScheduleRepository.findByUserId(userId).get();
        } catch (Exception e) {
            throw new NotFoundException("user id " + userId + " is not found");
        }
    }

    @Override
    @Transactional
    public Long updateSchedule(Request request, ZonedDateTime zonedDateTime, Long scheduleId) {
        updateMailSchedule(scheduleId,request);
//        JobDetail jobDetail = updateJobDetail(request,scheduleId);
//        updateTriggerDetails(request, jobDetail, zonedDateTime,scheduleId);
        return scheduleId;
    }

    public void updateMailSchedule(Long scheduleId, Request request) {
        MailSchedule mailSchedule = request.toMailSchedule(scheduleId);
        mailScheduleRepository.save(mailSchedule);
    }

    public JobDetail updateJobDetail(Request request, Long scheduleId) {
        JobDetail jobDetail = null;
        try {
            System.out.println("updateJobDetail");
            if (scheduleId != null) {
                jobDetail = scheduler.getJobDetail(new JobKey(scheduleId.toString(), JOB_DETAIL_GROUP_ID));
                jobDetail.getJobDataMap().put(TO_MAIL, request.getUserId());
                jobDetail.getJobDataMap().put(USERID, request.getUserId());
                jobDetail.getJobDataMap().put(MESSAGE, request.getMessage());
                scheduler.addJob(jobDetail, true);
            }
        } catch (SchedulerException schedulerException) {
            throw new InternalServerException("Unable to update the job data map");
        }
        return jobDetail;
    }

    public void updateTriggerDetails(Request request, JobDetail jobDetail, ZonedDateTime zonedDateTime, Long scheduleId) {
        Trigger newTrigger = getSimpleTrigger(jobDetail, zonedDateTime);
        System.out.println("updateTriggerDetails");
        try {
            scheduler.rescheduleJob(new TriggerKey(scheduleId.toString(), TRIGGER_GROUP_ID), newTrigger);
        } catch (SchedulerException schedulerException) {
            throw new InternalServerException("Unable to update the trigger in DB");
        }
    }

}
