package com.example.jobs;

import com.example.dao.MailScheduleDao;
import com.example.service.MailService;
import com.example.service.impl.TelegramServiceImpl;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import static com.example.utils.Constants.MailScheduleJob.*;

@Component
public class MailScheduleJob extends QuartzJobBean {

    private final MailService mailService;

    @Value("${spring.mail.username}")
    private String from;

    private final MailScheduleDao  mailScheduleDao;

    private final TelegramServiceImpl telegramService;

    public MailScheduleJob(MailService mailService, MailScheduleDao mailScheduleDao, TelegramServiceImpl telegramService) {
        this.mailService = mailService;
        this.mailScheduleDao = mailScheduleDao;
        this.telegramService = telegramService;
    }


    @Override
    public void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        String subject = jobDataMap.getString(USERID);
        String message = jobDataMap.getString(MESSAGE);
//        String toMail = jobDataMap.getString(TO_MAIL);
        String scheduleId = String.valueOf(jobDataMap.getLong(SCHEDULE_ID));

//        telegramService.sendMessage(message,"");
        mailService.sendMail(from, "sun.sythorng@gmail.com", subject, message);

        System.out.println("schedule job start working..!");
        mailScheduleDao.deleteMailSchedule(Long.valueOf(scheduleId));
    }
}
