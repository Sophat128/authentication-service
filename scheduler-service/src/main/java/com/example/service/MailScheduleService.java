package com.example.service;

import com.example.models.entity.MailSchedule;
import com.example.models.request.MessageRequest;
import com.example.models.request.Request;
import com.example.models.response.ApiResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface MailScheduleService {

    ApiResponse<?> createSchedule(Request request);

    ApiResponse<?>  getAllSchedulesByUserId(String userId,Integer pageNo,Integer pageSize);

    ApiResponse<?>  getSchedules(int page, int size);

    ApiResponse<?> updateScheduleById(Long scheduleId,Request request);

    ApiResponse<?> deleteScheduleById(Long id);
    MailSchedule getScheduleById(Long id);

    void validateLocalDateTimeWithZoneId(LocalDateTime localDateTime, String zoneId);

    Object createScheduleForAllUser(MessageRequest request);
}
