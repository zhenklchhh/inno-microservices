package com.innowise.authservice.service.impl;

import com.innowise.authservice.model.AccountDto;
import com.innowise.authservice.model.CreateUserRequestDto;
import com.innowise.authservice.model.RegistrationRequestDto;
import com.innowise.authservice.model.UserDto;
import com.innowise.authservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

    private final String userServiceUrl = "http://userservice:8080";

    public Mono<UserDto> registerUser(RegistrationRequestDto registrationRequestDto) {
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
                    try {
                        AccountDto accountDto = new AccountDto(
                                userResponse.id(),
                                registrationRequestDto.email(),
                                registrationRequestDto.password(),
                                registrationRequestDto.userRole()
                        );
                        accountService.createAccount(accountDto);
                        return Mono.empty(); // Успех
                    } catch (Exception e) {
                        return rollbackUserCreation(webClient, userResponse.id())
                                .then(Mono.error(e));
                    }
                });
    }

    private Mono<Void> rollbackUserCreation(WebClient webClient, Long userId) {;
        return webClient.delete()
                .uri("/users/" + userId)
                .retrieve()
                .bodyToMono(Void.class);
    }
}
