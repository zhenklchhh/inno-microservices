package com.innowise.orderservice.client;

import com.innowise.orderservice.model.UserDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author Evgeniy Zaleshchenok
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserClient {
    @Value("${user-service.url}")
    private String userServiceUrl;

    private final String USER_DATA_NOT_AVAILABLE = "User data not available";
    private final WebClient.Builder webClientBuilder;

    @CircuitBreaker(name = "userService", fallbackMethod = "fallbackGetUser")
    @Retry(name = "userService", fallbackMethod = "fallbackGetUser")
    public UserDto getUserByEmail(String email, String token) {
        WebClient webClient = webClientBuilder.baseUrl(userServiceUrl).build();
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/users")
                        .queryParam("email", email)
                        .build())
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();
    }

    private UserDto fallbackGetUser(String email, String token, Throwable throwable) {
        log.error("User service is unavailable. Falling back for user email: {}. Error: {}",
                email, throwable.getMessage());
        UserDto userDto = new UserDto();
        userDto.setEmail(email);
        userDto.setId(-1L);
        userDto.setName(USER_DATA_NOT_AVAILABLE);
        userDto.setSurname(USER_DATA_NOT_AVAILABLE);
        userDto.setPartial(true);
        return userDto;
    }
}
