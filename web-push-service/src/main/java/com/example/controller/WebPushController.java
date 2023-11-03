package com.example.controller;

import com.example.model.entities.WebDataConfig;
import com.example.model.request.PushNotificationRequest;
import com.example.model.request.WebConfigRequest;
import com.example.service.WebService;
import com.example.webpush.WebPushService;
import lombok.AllArgsConstructor;
import nl.martijndwars.webpush.Subscription;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.annotation.PutExchange;

import java.util.UUID;

@RestController
@AllArgsConstructor
public class WebPushController {
    private final WebPushService webPushService;
    private final WebService webService;

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

    @PostMapping("/addConfig")
    public void addWebConfig(@RequestBody WebConfigRequest webConfigRequest){
        webService.addConfig(webConfigRequest);
    }
    @PutMapping("/updateConfig/{id}")
    public WebDataConfig updateConfig(@RequestBody WebConfigRequest webConfigRequest, @PathVariable UUID id){
        return webService.updateConfig(id, webConfigRequest);
    }

    @GetMapping("/getConfig/{id}")
    public WebDataConfig getConfigById(@PathVariable UUID id){
        return webService.getConfigById(id);
    }

}
