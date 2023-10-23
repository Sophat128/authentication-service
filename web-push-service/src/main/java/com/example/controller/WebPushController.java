package com.example.controller;

import com.example.webpush.WebPushService;
import lombok.AllArgsConstructor;
import nl.martijndwars.webpush.Subscription;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class WebPushController {
    private final WebPushService webPushService;
    @PostMapping("/subscribe")
    public void subscribe(Subscription subscription){
        webPushService.subscribe(subscription);
    }
}
