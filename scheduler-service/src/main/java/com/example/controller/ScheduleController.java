package com.example.controller;

import com.example.models.request.Request;
import com.example.service.MailScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("api/v1/schedules")
public class ScheduleController {

    @Autowired
    private MailScheduleService mailScheduleService;

    @PostMapping("create")
    public ResponseEntity<?> createSchedule(@RequestBody Request request) {
        return ResponseEntity.ok().body(mailScheduleService.createSchedule(request));
    }

    @GetMapping("")
    public ResponseEntity<?> getSchedules(
            @RequestParam(defaultValue = "1", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok().body(mailScheduleService.getSchedules(page, size));
    }

    @GetMapping("{userId}")
    public ResponseEntity<?> getAllScheduleByUser(
            @PathVariable(value = "userId") UUID userId,
            @RequestParam(defaultValue = "1", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize
    ) {
        return ResponseEntity.ok().body(mailScheduleService.getAllSchedulesByUserId(String.valueOf(userId), pageNo, pageSize));
    }

    @PutMapping("{scheduleId}")
    public ResponseEntity<?> updateScheduleById(@PathVariable Long scheduleId, @RequestBody Request request) {
        return ResponseEntity.ok().body(mailScheduleService.updateScheduleById(scheduleId, request));
    }

    @DeleteMapping("{scheduleId}")
    public ResponseEntity<?> deleteSchedule(@PathVariable(value = "scheduleId") Long scheduleId) {
        return ResponseEntity.ok().body(mailScheduleService.deleteScheduleById(scheduleId));
    }
}
