package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SendEmailApplication {
    public static void main(String[] args) {
        SpringApplication.run(SendEmailApplication.class, args);
        System.out.println("Hello world!");
    }
}