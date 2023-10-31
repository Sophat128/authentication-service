package com.example;

import com.example.service.TaskService;
import com.example.service.impl.ScheduledJob;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@AllArgsConstructor
public class Main implements ApplicationRunner {
    private final TaskService taskService;
    private final ScheduledJob scheduledJob;
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        System.out.println("Hello world!");
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        taskService.scheduleTask();
//        scheduledJob.testing();
//        scheduledJob.sendNotification("Hello", "Email");

    }
}