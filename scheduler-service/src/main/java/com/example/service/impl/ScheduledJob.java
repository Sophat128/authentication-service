package com.example.service.impl;

import com.example.entity.NotificationType;
import com.example.entity.ScheduleTask;
import com.example.entity.UserEntity;
import com.example.service.TaskService;
import lombok.NoArgsConstructor;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;



@Component
//@PersistJobDataAfterExecution
//@DisallowConcurrentExecution
//@NoArgsConstructor
public class ScheduledJob extends QuartzJobBean {

    @Autowired
    private  TaskService taskService;
    @Autowired
    private  KafkaTemplate<String, Object> kafkaTemplate;

    public ScheduleTask testing(Long id){
        System.out.println("Data: " + taskService.getTaskById(2L));

        return taskService.getTaskById(id);
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        System.out.println("scheduleId: IS WORKING");

        // Retrieve the user ID from job data
        Long scheduleId = context.getJobDetail().getJobDataMap().getLong("scheduleId");
        System.out.println("scheduleId: " + scheduleId);

        // Retrieve user data based on the user ID
        UserEntity user = new UserEntity(1L, "Sophat");

        ScheduleTask scheduleTask = testing(scheduleId);
//                new ScheduleTask(2L, user, new Date(), "Hello", "Email", "Pending");
        System.out.println("Task; " + scheduleTask);

        // Send notifications to the user using user data
        sendNotification(scheduleTask.getNotificationContent(), scheduleTask.getNotificationType());

    }

    public void sendNotification(String data, String type){
        System.out.println("Data: " + data);
        if(NotificationType.EMAIL.name().equalsIgnoreCase(type)){
            System.out.println("It's working");
            Message<String> message = MessageBuilder
                    .withPayload(data)
                    .setHeader(KafkaHeaders.TOPIC, "send.email")
                    .build();
            System.out.println("Message: " + message);
            kafkaTemplate.send(message);
        }


    }
}
