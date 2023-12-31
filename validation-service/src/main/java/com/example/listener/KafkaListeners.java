package com.example.listener;

import com.example.repository.NewsRepository;
import com.example.service.WebClientService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class KafkaListeners {

    private final WebClientService webClientService;
    private final NewsRepository newsRepository;

    public KafkaListeners (
            WebClientService webClientService,
            NewsRepository newsRepository
    ) {
        this.webClientService = webClientService;
        this.newsRepository = newsRepository;
    }

    @KafkaListener(topics = "notification", groupId = "message-group")
    void listener(String date) {
        System.out.printf("Listener received: %s%n", date);

        Mono<ResponseEntity<String>> responseEntity = webClientService.sendRequest(date);

        responseEntity.subscribe(response -> {
            HttpStatus status = (HttpStatus) response.getStatusCode();
            if (status.equals(HttpStatus.OK)) {
                try {
                    newsRepository.saveNews(date, response.getBody())
                            .subscribe(isSaved -> {
                                if (isSaved)
                                    System.out.println("Data successfully saved in cache");
                                else
                                    System.out.println("Data save failed");
                            });
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println(status.value());
        });
    }
}
