package com.example.clienteventservice.controller;

import com.example.clienteventservice.model.entity.Subscription;
import com.example.clienteventservice.service.SubscriptionService;
import com.example.dto.SubscriptionDto;
import com.example.response.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("api/v1/clients")
@AllArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping("/get-notification/{userId}")
    public ResponseEntity<?> getNotificationTypeByUserId(@PathVariable String userId) {
        List<Subscription> subscriptionDtoList = subscriptionService.getNotificationTypeByUserId(userId);
        ApiResponse<List<Subscription>> response = ApiResponse.<List<Subscription>>builder()
                .message("success")
                .status(200)
                .payload(subscriptionDtoList)
                .build();
        return ResponseEntity.ok().body(response);
    }
}
