package com.example.controller;

import com.example.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping(value = "/message")
@AllArgsConstructor
public class NotificationController {
    private final NotificationService service;

    @GetMapping
    public Object getPublicKey(){
        System.out.println("Before");
        Object data = service.getPublicKey();
        System.out.println("Data: " + data);

        return data;
    }





}
