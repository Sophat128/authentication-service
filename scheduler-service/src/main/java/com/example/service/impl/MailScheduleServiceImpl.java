package com.example.service.impl;

import com.example.dao.MailScheduleDao;
import com.example.exception.BadRequestException;
import com.example.exception.NotFoundException;
import com.example.mapper.ScheduleMapper;
import com.example.models.entity.MailSchedule;
import com.example.models.request.MessageRequest;
import com.example.models.request.Request;
import com.example.models.response.ApiResponse;
import com.example.service.MailScheduleService;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

@Service
public class MailScheduleServiceImpl implements MailScheduleService {


    private final MailScheduleDao mailScheduleDao;

    public MailScheduleServiceImpl(MailScheduleDao mailScheduleDao) {
        this.mailScheduleDao = mailScheduleDao;
    }

    @Override
    public ApiResponse<?> createSchedule(Request request) {
        isValidUUID(String.valueOf(request.getUserId()));
        parseDateTime(String.valueOf(request.getScheduledTime()));
        isNotNullOrEmpty(request.getMessage(), "message");
        isValidZoneId(request.getZoneId());
        ZonedDateTime zonedDateTime = ZonedDateTime.of(request.getScheduledTime(), ZoneId.of(request.getZoneId()));

        validateLocalDateTimeWithZoneId(request.getScheduledTime(), request.getZoneId());
        String scheduleId = mailScheduleDao.createSchedule(request, zonedDateTime);
        return new ApiResponse<>("create schedule success", ScheduleMapper.toDto(getScheduleById(Long.valueOf(scheduleId))), HttpStatus.OK.value(), true);
    }

    @Override
    public ApiResponse<?> getAllSchedulesByUserId(String userId, Integer pageNo, Integer pageSize) {
        return new ApiResponse<>(
                "get all get all schedule by user id ",
                mailScheduleDao.getAllScheduleByUserId(userId, pageNo, pageSize).stream().map(ScheduleMapper::toDto).toList(),
                HttpStatus.OK.value(), true);
    }

    @Override
    public ApiResponse<?> getSchedules(int page, int size) {
        List<MailSchedule> mailSchedules = mailScheduleDao.getSchedules(page, size);
        if (mailSchedules.isEmpty()) {
            throw new NotFoundException("don't have schedule");
        }
        return new ApiResponse<>("get all get all schedule", mailSchedules.stream().map(ScheduleMapper::toDto).toList(), HttpStatus.OK.value(), true);
    }

    @Override
    public ApiResponse<?> updateScheduleById(Long scheduleId, Request request) {
        checkIfScheduleExists(scheduleId);
        mailScheduleDao.validateLocalDateTimeWithZoneId(request.getScheduledTime(), request.getZoneId());

        mailScheduleDao.deleteMailSchedule(scheduleId);
        mailScheduleDao.deleteJobAndTrigger(scheduleId);

        ZonedDateTime zonedDateTime = ZonedDateTime.of(request.getScheduledTime(), ZoneId.of(request.getZoneId()));
        JobDetail jobDetail = mailScheduleDao.getJobDetail(String.valueOf(scheduleId), request);
        Trigger simpleTrigger = mailScheduleDao.getSimpleTrigger(jobDetail, zonedDateTime);
        mailScheduleDao.scheduleJob(jobDetail, simpleTrigger);

        mailScheduleDao.updateSchedule(request, zonedDateTime, scheduleId);
        MailSchedule mailSchedule = getScheduleById(scheduleId);
        mailSchedule.setScheduleId(scheduleId);
        return new ApiResponse<>("update schedule id: " + scheduleId + " is Success", ScheduleMapper.toDto(mailSchedule), HttpStatus.OK.value(), true);
    }

    @Override
    public ApiResponse<?> deleteScheduleById(Long id) {
        checkIfScheduleExists(id);
        MailSchedule mailSchedule = getScheduleById(id);
        mailScheduleDao.deleteSchedule(id);
        return new ApiResponse<>("delete schedule id: " + id + " is Success", ScheduleMapper.toDto(mailSchedule), HttpStatus.OK.value(), true);
    }

    @Override
    public MailSchedule getScheduleById(Long id) {
        return mailScheduleDao.getScheduleById(id);
    }

    private void checkIfScheduleExists(Long id) {
        Boolean exists = mailScheduleDao.getScheduleByIdAndIsSendedFalse(id);
        if (!exists) {
            throw new BadRequestException(String.format("An active schedule with id %s does not exist", id));
        }
    }


    public static void isNotNullOrEmpty(String text, String field) {
        if (!(text != null && !text.trim().isEmpty())) {
            throw new BadRequestException(field + " Cannot null & empty");
        }
    }

    public static void parseDateTime(String dateTimeString) throws BadRequestException {
//        if(!dateTimeString.matches("\\\\d{4}-\\\\d{2}-\\\\d{2}T\\\\d{2}:\\\\d{2}:\\\\d{2}\\\\.\\\\d{3}")){
//            throw new BadRequestException("Invalid DateTime Format");
//        }
    }

    public static void isValidUUID(String uuidString) {
        try {
            UUID uuid = UUID.fromString(uuidString);
            // If the UUID is successfully parsed, it is valid
        } catch (Exception e) {
            throw new BadRequestException("UUID is invalid");
        }
    }

    public void validateLocalDateTimeWithZoneId(LocalDateTime localDateTime, String zoneId) {
        ZonedDateTime inputZonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.of(zoneId));
        ZonedDateTime currentZonedDateTime = ZonedDateTime.now(ZoneId.of(zoneId));

        LocalDateTime inputLocalDateTime = inputZonedDateTime.toLocalDateTime();
        LocalDateTime currentLocalDateTime = currentZonedDateTime.toLocalDateTime();

        if (inputZonedDateTime.isBefore(currentZonedDateTime)) {
            throw new BadRequestException("Can not input time in the past.");
        } else {
            if (inputLocalDateTime.getMinute() == currentLocalDateTime.getMinute()) {
                throw new BadRequestException("Can not input in the current time.");
            }
        }
    }

    @Override
    public Object createScheduleForAllUser(MessageRequest request) {
        parseDateTime(String.valueOf(request.getScheduledTime()));
        isNotNullOrEmpty(request.getMessage(), "message");
        isValidZoneId(request.getZoneId());
        ZonedDateTime zonedDateTime = ZonedDateTime.of(request.getScheduledTime(), ZoneId.of(request.getZoneId()));

        validateLocalDateTimeWithZoneId(request.getScheduledTime(), request.getZoneId());
        String scheduleId = mailScheduleDao.createScheduleForAll(request, zonedDateTime);
        return new ApiResponse<>("create schedule success", ScheduleMapper.messageDto(getScheduleById(Long.valueOf(scheduleId))), HttpStatus.OK.value(), true);

    }

    public static void isValidZoneId(String zoneIdString) {
        try {
            ZoneId zoneId = ZoneId.of(zoneIdString);
        } catch (Exception e) {
            throw new BadRequestException("invalid zone id [ UTC/Greenwich ] example -> [ Asia/Phnom_Penh ] ");
        }
    }
}
