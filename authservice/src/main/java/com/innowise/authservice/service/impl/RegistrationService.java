package com.innowise.authservice.service.impl;

import com.innowise.authservice.model.AccountDto;
import com.innowise.authservice.model.CreateUserRequestDto;
import com.innowise.authservice.model.RegistrationRequestDto;
import com.innowise.authservice.model.UserDto;
import com.innowise.authservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * @author Evgeniy Zaleshchenok
 */
@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final WebClient.Builder webClientBuilder;
    private final AccountService accountService;

    private final String BEARER_STRING = "Bearer ";

    @Value("${app.services.user-service.url}")
    private String userServiceUrl;

    public Mono<UserDto> registerUser(RegistrationRequestDto registrationRequestDto,
                                      String authToken) {
        WebClient webClient = webClientBuilder.baseUrl(userServiceUrl).build();
        CreateUserRequestDto createUserRequestDto = new CreateUserRequestDto(
                registrationRequestDto.email(),
                registrationRequestDto.name(),
                registrationRequestDto.surname(),
                registrationRequestDto.birthDate()
        );

        return webClient.post()
                .uri("/users")
                .bodyValue(createUserRequestDto)
                .retrieve()
                .bodyToMono(UserDto.class)
                .flatMap(userResponse -> {
                        AccountDto accountDto = new AccountDto(
                                userResponse.id(),
                                registrationRequestDto.email(),
                                registrationRequestDto.password(),
                                registrationRequestDto.userRole()
                        );
                        return Mono.fromRunnable(() -> accountService.createAccount(accountDto))
                                .thenReturn(userResponse)
                                .onErrorResume(error -> rollbackUserCreation(webClient, userResponse.id(),
                                        authToken)
                                .then(Mono.error(error)));
                });
    }

    private Mono<Void> rollbackUserCreation(WebClient webClient, Long userId, String token) {
        return webClient.delete()
                .uri("/users/" + userId)
                .header("Authorization", BEARER_STRING + token)
                .retrieve()
                .bodyToMono(Void.class);
    }
}
