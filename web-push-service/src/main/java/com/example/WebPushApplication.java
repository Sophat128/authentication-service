package com.example;

import com.example.model.entities.WebDataConfig;
import com.example.model.request.WebConfigRequest;
import com.example.service.WebService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing

public class WebPushApplication {



    public static void main(String[] args) {
        SpringApplication.run(WebPushApplication.class, args);
        System.out.println("Hello world!");
    }


}