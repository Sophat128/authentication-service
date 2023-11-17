package com.example.service.impl;

import com.example.exception.DownStreamServerException;
import com.example.response.ApiResponse;
import com.example.service.TelegramService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


@Service
public class TelegramServiceImpl implements TelegramService {

    private final WebClient.Builder webClient;

    public TelegramServiceImpl(WebClient.Builder webClient) {
        this.webClient = webClient;
    }

    public ApiResponse<?> sendMessage(String message, String jwt) {
        try {
            return webClient.baseUrl("http://localhost:8082/api/v1/telegram" + "/send-message")
//                .defaultHeaders(httpHeaders -> httpHeaders.setBearerAuth("eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJhT1Zkdll4dVp4UHZQdzRDVUJyb2twMHlfelh6UzBGTjY0dFNCOF9yRm9VIn0.eyJleHAiOjE2OTk5NDczOTIsImlhdCI6MTY5OTkyOTM5MiwianRpIjoiOGVhMGE4Y2QtOTJmNy00NWQwLWFmODctNGNiOGY3NjNkYjc2IiwiaXNzIjoiaHR0cHM6Ly9rZXljbG9hay5zeXRob3JuZy5zaXRlL2F1dGgvcmVhbG1zL1JlbnRIb21lIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6ImVjNzlhMjE0LWY1ODktNDM0OS04YTU5LWIwYTEwYzU0MTU1NSIsInR5cCI6IkJlYXJlciIsImF6cCI6InJlbnQtaG9tZS1hcHAiLCJzZXNzaW9uX3N0YXRlIjoiZTI0ZDUzZGItNGNmNS00ZTlhLWI1MzQtYWQ2NzllZWMxZDdlIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIqIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIiwiZGVmYXVsdC1yb2xlcy1yZW50aG9tZSJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoiZW1haWwgcHJvZmlsZSIsInNpZCI6ImUyNGQ1M2RiLTRjZjUtNGU5YS1iNTM0LWFkNjc5ZWVjMWQ3ZSIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwicHJlZmVycmVkX3VzZXJuYW1lIjoidGhvcm5nIiwiZW1haWwiOiJzdW4uc3l0aG9ybmdAZ21haWwuY29tIn0.Impe6837FTOgTAi29mrb-dndT_NJWh4Vd-hM8TFAyg7a3H0VIZ5v73SMcX3WKp7D1O8nbNhLFaY9_NdpxPWGpMiu9NNi7zTE4nESK44vcrAN6krnRv3PLWJlPPAcB3AgXJISCVdZYZjVkynFbgy5kE6DIdSGq_KBb4b-E11fG8-Jef7A8zkyxlQrhFoTJBGjIJu_TUoN26wVEl0ZkneX91LyDZm_5oUggWJvuNtsv4xL6OvwIJWte4HnW810yBxJfgk7VVQaG5FlC5cHrrFiQWHv8jGYGySnnUYKDKHW1mf2J-WUFO7J4RX09gasNsDoZ2bLPxVVrpXco8rPFvzX5A"))
                    .build()
                    .post()
                    .uri("?message=" + message+"&token="+"eyJhb")
                    .retrieve()
                    .bodyToMono(ApiResponse.class).block();
        }catch (Exception e){
            throw  new DownStreamServerException("Telegram service down");
        }

    }
    public void  get() {
        System.out.println("Object: "+
                webClient.baseUrl("http://localhost:8082/api/v1/telegram" )
                        .build()
                        .post()
                        .uri("/get")
                        .retrieve()
                        .bodyToMono(Object.class).block()
        );
    }
}
