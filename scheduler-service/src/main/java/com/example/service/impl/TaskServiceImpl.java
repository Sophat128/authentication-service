package com.example.service.impl;

import com.example.entity.ResponseStatus;
import com.example.entity.ScheduleTask;
import com.example.entity.Status;
import com.example.exception.NotFoundException;
import com.example.repository.ScheduleRepository;
import com.example.response.TaskResponse;
import com.example.service.TaskService;
import lombok.AllArgsConstructor;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskServiceImpl.class);
    private final ScheduleRepository scheduleRepository;



    @Override
    public TaskResponse scheduleTask(){
        TaskResponse taskResponse;

        try {
            // Create a Scheduler
            SchedulerFactory schedulerFactory = new StdSchedulerFactory();
            Scheduler scheduler = schedulerFactory.getScheduler();

            List<ScheduleTask> scheduleTasks = scheduleRepository.findAll();

            // Iterate through user schedules
            for (ScheduleTask userSchedule : scheduleTasks) {
                // Define the Job
                JobDetail job = JobBuilder.newJob(ScheduledJob.class)
                        .withIdentity("notificationJob_" + userSchedule.getUser().getUserId(), "group1")
                        .usingJobData("scheduleId", userSchedule.getScheduleId())
                        .build();

                // Define the Trigger for each user's schedule
                Trigger trigger = TriggerBuilder.newTrigger()
                        .withIdentity("notificationTrigger_" + userSchedule.getScheduleId(), "group1")
                        .startAt(specificDate()) // Set the user's specific date and time
                        .build();

                // Schedule the Job with the Trigger
                scheduler.scheduleJob(job, trigger);
                // Define the Job
            }

            // Start the Scheduler
            taskResponse = TaskResponse.builder()
                    .status(ResponseStatus.builder()
                            .status(Status.SUCCEED)
                            .build())
                    .build();
            LOGGER.info("Task submitted successfully : "+ taskResponse);
            scheduler.start();
        } catch (Exception e) {
            taskResponse = TaskResponse.builder()
                    .status(ResponseStatus.builder()
                            .status(Status.FAILED)
                            .build())
                    .build();
            LOGGER.error("Task could not be submitted : "+ e.getMessage());
        }
        return taskResponse;

        // Helper method to set the specific date and time (e.g., October 30, 2023, at 5:00 PM)

    }

    @Override
    public ScheduleTask getTaskById(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NotFoundException("ScheduleTask with ID " + scheduleId + " not found"));
    }

    private static Date specificDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 10); // Add one minute to the current time
        return calendar.getTime();
//        java.util.Calendar calendar = java.util.Calendar
//                .getInstance();
//        calendar.set(2023, Calendar.OCTOBER, 30, 17, 0); // Year, Month (0-based), Day, Hour, Minute
//        return calendar.getTime();
    }




}
