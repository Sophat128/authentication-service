package com.example.jobs;

import com.example.config.WebClientConfig;
import com.example.dao.MailScheduleDao;
import com.example.models.dto.MessageDto;
import com.example.models.dto.ScheduleDto;
import com.example.models.request.MessageRequest;
import com.example.service.MailService;
import com.example.service.impl.TelegramServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

import static com.example.utils.Constants.MailScheduleJob.*;

@Component
@Slf4j

public class MailScheduleJob extends QuartzJobBean {

    private static final String NOTIFICATION_TOPIC = "notification-service-schedule";
    private static final String TELEGRAM_TOPIC = "telegram-schedule";
    private static final String EMAIL_TOPIC = "email-schedule";
    private static final String WEB_TOPIC = "web-notification-schedule";
    private final WebClientConfig webClientConfig;

    private final MailService mailService;

    @Value("${spring.mail.username}")
    private String from;

    private final MailScheduleDao mailScheduleDao;
    private final KafkaTemplate<String, String> kafkaTemplate;


    private final TelegramServiceImpl telegramService;

    public MailScheduleJob(WebClientConfig webClientConfig, MailService mailService, MailScheduleDao mailScheduleDao, KafkaTemplate<String, String> kafkaTemplate, TelegramServiceImpl telegramService) {

        this.webClientConfig = webClientConfig;
        this.mailService = mailService;
        this.mailScheduleDao = mailScheduleDao;
        this.kafkaTemplate = kafkaTemplate;
        this.telegramService = telegramService;
    }


    @Override
    public void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        String userId = jobDataMap.getString(USERID);
        String message = jobDataMap.getString(MESSAGE);
//        String toMail = jobDataMap.getString(TO_MAIL);
        String scheduleId = String.valueOf(jobDataMap.getLong(SCHEDULE_ID));

        ScheduleDto schedule = new ScheduleDto(userId, message);
//        MessageDto messageDto = new MessageDto(message);

//        telegramService.sendMessage(message,"");
//        mailService.sendMail(from, "sun.sythorng@gmail.com", userId, message);

//        if (userId == null) {
//            Message<String> webResponse = MessageBuilder
//                    .withPayload(messageDto.toString())
//                    .setHeader(KafkaHeaders.TOPIC, NOTIFICATION_TOPIC)
//                    .build();
//            System.out.println("Message: " + message);
//            kafkaTemplate.send(webResponse);
//        }

        Message<String> webResponse = MessageBuilder
                .withPayload(schedule.toString())
                .setHeader(KafkaHeaders.TOPIC, NOTIFICATION_TOPIC)
                .build();
        System.out.println("Message: " + message);
        kafkaTemplate.send(webResponse);


//        Message<ScheduleDto> emailResponse = MessageBuilder
//                .withPayload(schedule)
//                .setHeader(KafkaHeaders.TOPIC, EMAIL_TOPIC)
//                .build();
//        System.out.println("Message: " + emailResponse);
//        kafkaTemplate.send(emailResponse);
//
//        String subscriptionUrl = "http://client-event-service/api/v1/clients/get-notification";
//        WebClient web = webClientConfig.webClientBuilder().baseUrl(subscriptionUrl).build();
//
//        ApiResponse<List<Map<String, Object>>> subscriptionDtos = web.get()
//                .uri("/{userId}", userId)
//                .retrieve()
//                .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<Map<String, Object>>>>() {})
//                .block();
//
//        assert subscriptionDtos != null;
//        List<Map<String, Object>> payload = subscriptionDtos.getPayload();
//        List<String> notificationTypes = payload.stream()
//                .map(subscription -> (String) subscription.get("notificationType"))
//                .toList();
//
//        System.out.println("Notification: " + notificationTypes);
//
//        for (String type : notificationTypes) {
//            System.out.println("Type: " + type);
//            log.info("Processing notificationType: {}", type);
//            if (type.equals("TELEGRAM")) {
//                Message<ScheduleDto> telegramResponse = MessageBuilder
//                        .withPayload(schedule)
//                        .setHeader(KafkaHeaders.TOPIC, TELEGRAM_TOPIC)
//                        .build();
//                System.out.println("Message: " + telegramResponse);
//                kafkaTemplate.send(emailResponse);
//                log.info("Sent message to TELEGRAM_TOPIC: {}", telegramResponse);
////            } else if (type.equals("EMAIL")) {
////                kafkaTemplate.send(EMAIL_TOPIC, notification.key(), notification.value());
////                log.info("Sent message to EMAIL_TOPIC: {}", notification.value());
//            } else {
//                log.info("this userId doesn't have subscribe telegram or email notification types!");
//            }
//        }

        System.out.println("schedule job start working..!");
        mailScheduleDao.deleteMailSchedule(Long.valueOf(scheduleId));
    }
}
