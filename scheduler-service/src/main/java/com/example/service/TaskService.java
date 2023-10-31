package com.example.service;

import com.example.entity.ResponseStatus;
import com.example.entity.ScheduleTask;
import com.example.response.TaskResponse;

public interface TaskService {
    TaskResponse scheduleTask();
    ScheduleTask getTaskById(Long scheduleId);
}
