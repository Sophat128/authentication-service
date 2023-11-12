package com.example.webpush;

import com.example.model.entities.WebDataConfig;
import com.example.model.request.PushNotificationRequest;
import com.example.model.entities.UserSubscription;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class WebPushService {

    //    @Value("${vapid.public.key}")
//    private String publicKey;
//    @Value("${vapid.private.key}")
//    private String privateKey;
//    @Value("${vapid.subject}")
//    private String subject;
    private final WebService webService;
    private PushService pushService;
    private final Logger logger = LoggerFactory.getLogger(WebPushService.class);

    private final Map<String, Subscription> endpointToSubscription = new HashMap<>();
    private final WebRepository webRepository;

    public WebPushService(WebService webService, WebRepository webRepository) {
        this.webService = webService;
        this.webRepository = webRepository;
    }

    @PostConstruct
    private void init() throws GeneralSecurityException {
        WebDataConfig webDataConfig = new WebDataConfig();
        String subject = "http://localhost:64114";
        try {
            webDataConfig = webService.getConfig();
            Security.addProvider(new BouncyCastleProvider());
            pushService = new PushService(webDataConfig.getPublicKey(), webDataConfig.getPrivateKey(), subject);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            logger.error("Error initializing PushService: " + e.getMessage(), e);
        }

    }

//    public void reinitializeConfig() throws GeneralSecurityException {
//        WebDataConfig webDataConfig = webService.getConfig();
//        Security.addProvider(new BouncyCastleProvider());
//        pushService = new PushService(webDataConfig.getPublicKey(), webDataConfig.getPrivateKey());
//    }
//    public String getPublicKey() {
//        return publicKey;
//    }

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

    public void clearAllSubscription() {
        endpointToSubscription.clear();
    }

    public void subscribe(Subscription subscription, String userId) {
        System.out.println("Subscribed to " + subscription.endpoint);
        System.out.println("auth to " + subscription.keys.auth);
        System.out.println("p256dh to " + subscription.keys.p256dh);
//        The userId should be get from web application but I just give the default
        UserSubscription userSubscription = new UserSubscription(subscription.endpoint, subscription.keys.auth, subscription.keys.p256dh, userId);
        webRepository.save(userSubscription);

        endpointToSubscription.put(subscription.endpoint, subscription);
    }

    public void unsubscribe(String endpoint) {
        webRepository.deleteByEndpointContains(endpoint);
        String subscriptionPrefix = "https://fcm.googleapis.com/fcm/send/";
        System.out.println("Unsubscribed " + subscriptionPrefix + endpoint);
    }

    public record Message(String title, String body) {
    }

    ObjectMapper mapper = new ObjectMapper();

    public void notifyAll(PushNotificationRequest pushNotificationRequest) {
        try {
            String msg = mapper.writeValueAsString(new Message(pushNotificationRequest.getTitle(), pushNotificationRequest.getBody()));
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
            throw new RuntimeException(e);
        }
    }
}
