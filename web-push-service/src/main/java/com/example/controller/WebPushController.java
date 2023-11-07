package com.example.controller;

import com.example.model.entities.UserSubscription;
import com.example.model.entities.WebDataConfig;
import com.example.model.request.PushNotificationRequest;
import com.example.model.request.WebConfigRequest;
import com.example.model.respone.ApiResponse;
import com.example.service.WebService;
import com.example.webpush.WebPushService;
import lombok.AllArgsConstructor;
import nl.martijndwars.webpush.Subscription;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.annotation.PutExchange;

import java.security.GeneralSecurityException;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/webpush")
public class WebPushController {
    private final WebPushService webPushService;
    private final WebService webService;

    @GetMapping("/publicKey")
    public ResponseEntity<?> getPublicKey() {
        try {
            ApiResponse<?> data = new ApiResponse<>("publicKey", HttpStatus.OK.value(), webService.getConfig().getPublicKey());
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get Public key " + e.getMessage());
        }
    }

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@RequestBody Subscription subscription) {
        try {
            webPushService.subscribe(subscription);
            ApiResponse<?> data = new ApiResponse<>("Subscribe successfully", HttpStatus.OK.value());
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to subscribe " + e.getMessage());
        }
    }

    @GetMapping("/getAllSubscriber")
    public ResponseEntity<?> getAllSubscription() {
        try {
            List<UserSubscription> subscribers = webPushService.getAllSubscriber();
            ApiResponse<?> data = new ApiResponse<>("Get all subscribers successfully", HttpStatus.OK.value(), subscribers);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get all subscribers  " + e.getMessage());
        }
    }

    @PostMapping("/unsubscribe")
    public ResponseEntity<?> unsubscribe(@RequestBody Subscription subscription) {
        try {
            webPushService.unsubscribe(subscription);
            ApiResponse<?> data = new ApiResponse<>("Unsubscribe successfully", HttpStatus.OK.value());
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to unsubscribe " + e.getMessage());
        }
    }

    @PostMapping("/send_notification")
    public ResponseEntity<?> sendNotification(@RequestBody PushNotificationRequest pushNotificationRequest) {
        try {
            webPushService.notifyAll(pushNotificationRequest);
            ApiResponse<?> data = new ApiResponse<>("Alert to all subscribers successfully", HttpStatus.OK.value());
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to alert to all subscribers " + e.getMessage());
        }

    }

    @PostMapping("/send_notification_user")
    public ResponseEntity<?> sendNotificationToSpecificUser(@RequestBody PushNotificationRequest pushNotificationRequest) {
        try {
            webPushService.notifySpecificUser(pushNotificationRequest);
            ApiResponse<?> data = new ApiResponse<>("Alert to a subscriber successfully", HttpStatus.OK.value());
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to alert to a subscriber " + e.getMessage());
        }

    }

    @PostMapping("/addConfig")
    public ResponseEntity<?> addWebConfig(@RequestBody WebConfigRequest webConfigRequest) {
        try {
            webService.addConfig(webConfigRequest);
            ApiResponse<?> data = new ApiResponse<>("Add configuration successfully", HttpStatus.OK.value());
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add configuration " + e.getMessage());
        }
    }

    @PutMapping("/updateConfig/{id}")
    public ResponseEntity<?> updateConfig(@RequestBody WebConfigRequest webConfigRequest, @PathVariable UUID id) {
        try {
            WebDataConfig webDataConfig = webService.updateConfig(id, webConfigRequest);
            ApiResponse<?> data = new ApiResponse<>("Update configuration successfully", HttpStatus.OK.value(), webDataConfig);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update configuration " + e.getMessage());
        }
    }

    @GetMapping("/getConfig/{id}")
    public ResponseEntity<?> getConfigById(@PathVariable UUID id) {
        try {
            WebDataConfig webDataConfig = webService.getConfigById(id);

            ApiResponse<?> data = new ApiResponse<>("Get configuration successfully", HttpStatus.OK.value(), webDataConfig);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get configuration " + e.getMessage());
        }
    }

}
