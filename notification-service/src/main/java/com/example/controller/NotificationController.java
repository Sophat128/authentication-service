//package com.example.controller;
//
//import com.example.entities.request.EmailRequest;
//import com.example.service.NotificationService;
//import lombok.AllArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping(value = "/message")
//@AllArgsConstructor
//public class NotificationController {
//
//    private final NotificationService service;
//
//    @PostMapping("/send_mail")
//    public ResponseEntity<?> sendMail(@RequestBody EmailRequest emailRequest) {
//        service.publishToMail(emailRequest);
//
//        return ResponseEntity.ok("Successful");
//
//    }
//
//
//
//
//}
