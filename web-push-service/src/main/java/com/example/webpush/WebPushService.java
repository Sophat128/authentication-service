package com.example.webpush;

import com.example.entities.request.PushNotificationRequest;
import com.example.entities.request.UserSubscription;
import com.example.repository.WebRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jose4j.lang.JoseException;
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
public class WebPushService {

    @Value("${vapid.public.key}")
    private String publicKey;
    @Value("${vapid.private.key}")
    private String privateKey;
    @Value("${vapid.subject}")
    private String subject;
    private PushService pushService;

    private final Map<String, Subscription> endpointToSubscription = new HashMap<>();
    private final WebRepository webRepository;

    public WebPushService(WebRepository webRepository) {
        this.webRepository = webRepository;
    }

    @PostConstruct
    private void init() throws GeneralSecurityException {
        Security.addProvider(new BouncyCastleProvider());
        pushService = new PushService(publicKey, privateKey, subject);
    }

    public String getPublicKey() {
        return publicKey;
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

    public void getAllSubscription() {

        for (Map.Entry<String, Subscription> entry : endpointToSubscription.entrySet()) {
            String endpoint = entry.getKey();
            Subscription subscription = entry.getValue();
            // Now you can work with 'endpoint' and 'subscription' as needed
            System.out.println("Endpoint: " + endpoint);
            System.out.println("Subscription: " + subscription.keys.auth);
        }
    }

    public void clearAllSubscription() {
        endpointToSubscription.clear();
    }

    public void subscribe(Subscription subscription) {
        System.out.println("Subscribed to " + subscription.endpoint);
        System.out.println("auth to " + subscription.keys.auth);
        System.out.println("p256dh to " + subscription.keys.p256dh);
//        The userId should be get from web application but I just give the default
        UserSubscription userSubscription = new UserSubscription(subscription.endpoint, subscription.keys.auth, subscription.keys.p256dh, 1L);
        if (webRepository.findByUserId(1L) != null) {
            webRepository.save(userSubscription);
        }else {
            System.out.println("This user is already subscribe");
        }
        endpointToSubscription.put(subscription.endpoint, subscription);
    }

    public void unsubscribe(Subscription subscription) {
        System.out.println("Unsubscribed " + subscription.endpoint + " auth:" + subscription.keys.auth);
        endpointToSubscription.remove(subscription.endpoint);
    }

    public record Message(String title, String body) {
    }

    ObjectMapper mapper = new ObjectMapper();

    public void notifyAll(PushNotificationRequest pushNotificationRequest) {

        try {
            String msg = mapper.writeValueAsString(new Message(pushNotificationRequest.getTitle(), pushNotificationRequest.getBody()));
            System.out.println("Data: " + msg);
            endpointToSubscription.values().forEach(subscription -> {
                System.out.println("Subscription: " + subscription.keys.auth);
                sendNotification(subscription, msg);
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void notifySpecificUser(PushNotificationRequest pushNotificationRequest) {
        try {
            String msg = mapper.writeValueAsString(new Message(pushNotificationRequest.getTitle(), pushNotificationRequest.getBody()));
//            Give default user id
            UserSubscription userSubscription = webRepository.findByUserId(1L);

            Subscription subscription = new Subscription(userSubscription.getEndpoint(), new Subscription.Keys(userSubscription.getP256dh(), userSubscription.getAuth()));
            System.out.println("Auth: " + subscription.keys.auth);
            sendNotification(subscription, msg);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
