package com.example.webpush;

import com.example.model.dto.ScheduleDto;
import com.example.model.dto.TransactionHistoryDto;
import com.example.model.entities.WebPushHistory;
import com.example.model.request.PushNotificationRequest;
import com.example.model.entities.UserSubscription;
import com.example.model.request.WebConfigRequest;
import com.example.repository.WebConfigRepository;
import com.example.repository.WebPushHistoryRepository;
import com.example.repository.WebRepository;
import com.example.service.WebService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class WebPushService {

    private final WebService webService;
    private PushService pushService;

    private final Logger logger = LoggerFactory.getLogger(WebPushService.class);

    private final WebRepository webRepository;
    private final WebConfigRepository webConfigRepository;
    private final WebPushHistoryRepository webPushHistoryRepository;

    @Value("${vapid.private.key}")
    private String PRIVATE_KEY;
    @Value("${vapid.public.key}")
    private String PUBLIC_KEY;

    public WebPushService(WebService webService, WebRepository webRepository, WebConfigRepository webConfigRepository, WebPushHistoryRepository webPushHistoryRepository) {
        this.webService = webService;
        this.webRepository = webRepository;
        this.webConfigRepository = webConfigRepository;
        this.webPushHistoryRepository = webPushHistoryRepository;
    }

    @PostConstruct
    private void init() throws GeneralSecurityException {

        WebConfigRequest webDataConfig = new WebConfigRequest(PRIVATE_KEY, PUBLIC_KEY);
        if (!(webConfigRepository.findAll().size() > 0)) {
            webService.addConfig(webDataConfig);
        }
        try {
            Security.addProvider(new BouncyCastleProvider());
            pushService = new PushService(webDataConfig.getPublicKey(), webDataConfig.getPrivateKey());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            logger.error("Error initializing PushService: " + e.getMessage(), e);
        }

    }

    public void sendNotification(Subscription subscription, String messageJson) {
        try {
            System.out.println("user subscription: " + subscription.keys.auth);
            HttpResponse response = pushService.send(new Notification(subscription, messageJson));
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 201) {
                System.out.println("Server error, status code:" + statusCode);
                InputStream content = response.getEntity().getContent();
                List<String> strings = IOUtils.readLines(content, "UTF-8");
                System.out.println(strings);
            }
        } catch (GeneralSecurityException | IOException | JoseException | ExecutionException
                 | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public List<UserSubscription> getAllSubscriber() {
        return webRepository.findAll();
    }


    public void subscribe(Subscription subscription, String userId) {
        System.out.println("Subscribed to " + subscription.endpoint);
        System.out.println("auth to " + subscription.keys.auth);
        System.out.println("p256dh to " + subscription.keys.p256dh);
        UserSubscription userSubscription = new UserSubscription(subscription.endpoint, subscription.keys.auth, subscription.keys.p256dh, userId);
        webRepository.save(userSubscription);

    }

    public void unsubscribe(String endpoint) {
        webRepository.deleteByEndpointContains(endpoint);
        String subscriptionPrefix = "https://fcm.googleapis.com/fcm/send/";
        System.out.println("Unsubscribed " + subscriptionPrefix + endpoint);
    }

    public void saveWebHistory(WebPushHistory webPushHistory) {
        webPushHistory.setStatus("Delivered");
        webPushHistoryRepository.save(webPushHistory);
    }

    public record Message(String title, TransactionHistoryDto body) {
    }


    ObjectMapper mapper = new ObjectMapper();

    public void notifyAll(ScheduleDto message) {
        try {
            String msg = mapper.writeValueAsString(message);

            System.out.println("Data: " + msg);
            getAllSubscriber().forEach(userSubscription -> {
                Subscription subscription = new Subscription(userSubscription.getEndpoint(), new Subscription.Keys(userSubscription.getP256dh(), userSubscription.getAuth()));
                System.out.println("Subscription: " + subscription.keys.auth);
                sendNotification(subscription, msg);
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void notifySpecificUser(PushNotificationRequest pushNotificationRequest, String userId) {
        try {
            String msg = mapper.writeValueAsString(new Message(pushNotificationRequest.getTitle(), pushNotificationRequest.getBody()));
            List<UserSubscription> userSubscriptions = webRepository.findByUserId(userId);
            userSubscriptions.forEach(userSubscription -> {
                Subscription subscription = new Subscription(userSubscription.getEndpoint(), new Subscription.Keys(userSubscription.getP256dh(), userSubscription.getAuth()));
                System.out.println("Subscription: " + subscription.keys.auth);
                sendNotification(subscription, msg);
            });
        } catch (JsonProcessingException e) {
            System.out.println("Error");
            throw new RuntimeException(e);
        }
    }

    public void notifySpecificUserWithSchedule(ScheduleDto scheduleDto) {
        System.out.println("Working now");
        try {
            String msg = mapper.writeValueAsString(scheduleDto);
            List<UserSubscription> userSubscriptions = webRepository.findByUserId(scheduleDto.getUserId());
            userSubscriptions.forEach(userSubscription -> {
                Subscription subscription = new Subscription(userSubscription.getEndpoint(), new Subscription.Keys(userSubscription.getP256dh(), userSubscription.getAuth()));
                System.out.println("Subscription: " + subscription.keys.auth);
                sendNotification(subscription, msg);
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
