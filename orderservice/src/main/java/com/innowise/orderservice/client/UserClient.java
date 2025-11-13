package com.innowise.orderservice.client;

import com.innowise.orderservice.exception.ServiceUnavailableException;
import com.innowise.orderservice.exception.UserNotFoundInUserServiceException;
import com.innowise.orderservice.model.UserDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * @author Evgeniy Zaleshchenok
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserClient {
    private final String USER_DATA_NOT_AVAILABLE = "User data not available";
    private final WebClient.Builder webClientBuilder;
    @Value("${user-service.url}")
    private String userServiceUrl;

    @CircuitBreaker(name = "userService", fallbackMethod = "fallbackForSystemFailrue")
    @Retry(name = "userService", fallbackMethod = "fallbackForSystemFailrue")
    public UserDto getUserByEmail(String email, String token) {
        WebClient webClient = webClientBuilder.baseUrl(userServiceUrl).build();
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/users")
                        .queryParam("email", email)
                        .build())
                .header("Authorization", token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    if (response.statusCode().equals(HttpStatus.NOT_FOUND)){
                        throw new UserNotFoundInUserServiceException("User with email " + email + " not found");
                    }
                    return response.bodyToMono(String.class)
                            .flatMap(errorBody -> Mono.error(new IllegalStateException("Client error: " + errorBody )));
                })
                .bodyToMono(UserDto.class)
                .block();
    }

    private UserDto fallbackForSystemFailrue(String email, String token, Throwable throwable) {
        log.error("User service is unavailable. Falling back for user email: {}. Error: {}",
                email, throwable.getMessage());
        if (throwable instanceof UserNotFoundInUserServiceException ||
                (throwable.getCause() != null && throwable.getCause() instanceof UserNotFoundInUserServiceException)) {
            if (throwable instanceof UserNotFoundInUserServiceException ex) {
                throw ex;
            } else {
                throw (UserNotFoundInUserServiceException) throwable.getCause();
            }
        }
        UserDto userDto = new UserDto();
        userDto.setEmail(email);
        userDto.setId(-1L);
        userDto.setName(USER_DATA_NOT_AVAILABLE);
        userDto.setSurname(USER_DATA_NOT_AVAILABLE);
        userDto.setPartial(true);
        return userDto;
    }
}
