package com.example.controller;

import com.example.entities.request.PushNotificationRequest;
import com.example.webpush.WebPushService;
import lombok.AllArgsConstructor;
import nl.martijndwars.webpush.Subscription;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class WebPushController {
    private final WebPushService webPushService;

    @GetMapping("/publicKey")
    public String getPublicKey(){
        return webPushService.getPublicKey();
    }

    @PostMapping("/subscribe")
    public void subscribe(@RequestBody Subscription subscription) {
        webPushService.subscribe(subscription);
        System.out.println("successfully subscribe");
    }

    @GetMapping("/getAllSubscription")
    public void getAllSubscription(){
        webPushService.getAllSubscription();
    }

    @GetMapping("/clearAllSubscription")
    public void clearAllSubscription(){
        webPushService.clearAllSubscription();
    }


    @PostMapping("/unsubscribe")
    public void unsubscribe(@RequestBody Subscription subscription) {
        webPushService.unsubscribe(subscription);
        System.out.println("successfully unsubscribe");
    }

    @PostMapping("/send_notification")
    public void sendNotification(@RequestBody PushNotificationRequest pushNotificationRequest) {
        webPushService.notifyAll(pushNotificationRequest);

    }

    @PostMapping("/send_notification_user")
    public void sendNotificationToSpecificUser(@RequestBody PushNotificationRequest pushNotificationRequest) {
        webPushService.notifySpecificUser(pushNotificationRequest);

    }
}
