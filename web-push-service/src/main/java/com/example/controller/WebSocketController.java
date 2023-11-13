package com.example.controller;

import com.example.service.WebService;
import com.example.webpush.WebPushService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class WebSocketController {
    private final WebPushService webService;

    @MessageMapping("/getAllSubscribers")
    @SendTo("/topic/getAllSubscribers")
    public Integer getAllSubscribers() {
       return webService.getAllSubscriber().size();
        // Implement logic to add a subscriber
    }

    @MessageMapping("/removeSubscriber")
    @SendTo("/topic/subscribers")
    public String removeSubscriber(String subscriberId) {
        // Implement logic to remove a subscriber
        return "Subscriber removed: " + subscriberId;
    }
}
