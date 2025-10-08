package com.innowise.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.userservice.model.CardDto;
import com.innowise.userservice.model.entity.Card;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.repository.CardRepository;
import com.innowise.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Evgeniy Zaleshchenok
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @Container
    static PostgreSQLContainer<?> database = new PostgreSQLContainer<>("postgres:13-alpine");
    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:6-alpine")
            .withExposedPorts(6379);


    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", database::getJdbcUrl);
        registry.add("spring.datasource.username", database::getUsername);
        registry.add("spring.datasource.password", database::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379).toString());
    }

    @BeforeEach
    void setUp() {
        cardRepository.deleteAll();
        userRepository.deleteAll();
        testUser = userRepository.save(new User("Test", "User", LocalDate.now(), "test.user@example.com"));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void createCard_withValidData_returnsCreated() throws Exception {
        CardDto cardDto = new CardDto();
        cardDto.setUserId(testUser.getId());
        cardDto.setCardNumber("1111-2222-3333-4444");
        cardDto.setHolder("Test User");
        cardDto.setExpiryDate(LocalDate.now().plusYears(1));

        mockMvc.perform(post("/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.cardNumber").value("1111-2222-3333-4444"));
    }

    @Test
    void createCard_withInvalidData_returnsBadRequest() throws Exception {
        CardDto cardDto = new CardDto();
        cardDto.setUserId(testUser.getId());
        cardDto.setCardNumber("INVALID-NUMBER-FORMAT");
        cardDto.setHolder("");
        cardDto.setExpiryDate(LocalDate.now().minusDays(1));

        mockMvc.perform(post("/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCard_whenUserNotFound_returnsNotFound() throws Exception {
        CardDto cardDto = new CardDto();
        cardDto.setUserId(999L);
        cardDto.setCardNumber("1111-2222-3333-4444");
        cardDto.setHolder("Test User");
        cardDto.setExpiryDate(LocalDate.now().plusYears(1));

        mockMvc.perform(post("/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCardById_whenCardExists_returnsCard() throws Exception {
        Card savedCard = cardRepository.save(new Card(testUser, "1111", "Holder", LocalDate.now()));

        mockMvc.perform(get("/cards/{id}", savedCard.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedCard.getId()))
                .andExpect(jsonPath("$.holder").value("Holder"));
    }

    @Test
    void getCardById_whenCardNotFound_returnsNotFound() throws Exception {
        mockMvc.perform(get("/cards/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllCards_whenCardsExist_returnsCardList() throws Exception {
        cardRepository.saveAll(List.of(
                new Card(testUser, "1111", "Holder", LocalDate.now()),
                new Card(testUser, "2222", "Holder", LocalDate.now())
        ));

        mockMvc.perform(get("/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getCardsByIds_whenCardsExist_returnsCardList() throws Exception {
        Card card1 = cardRepository.save(new Card(testUser, "1111", "Holder", LocalDate.now()));
        Card card2 = cardRepository.save(new Card(testUser, "2222", "Holder", LocalDate.now()));

        mockMvc.perform(get("/cards")
                        .param("ids", String.valueOf(card1.getId()), String.valueOf(card2.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void updateCard_whenCardExists_returnsUpdatedCard() throws Exception {
        Card savedCard = cardRepository.save(new Card(testUser, "1234", "Holder", LocalDate.now()));
        CardDto updateDto = new CardDto();
        updateDto.setHolder("Updated Holder");
        updateDto.setUserId(testUser.getId());
        updateDto.setCardNumber("123456789123456789");
        updateDto.setExpiryDate(LocalDate.now().plusYears(2));

        mockMvc.perform(put("/cards/{id}", savedCard.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.holder").value("Updated Holder"));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void updateCard_whenCardNotFound_returnsNotFound() throws Exception {
        CardDto updateDto = new CardDto();
        updateDto.setHolder("Wont Update");
        updateDto.setUserId(testUser.getId());
        updateDto.setCardNumber("123456789123456789");
        updateDto.setExpiryDate(LocalDate.now().plusYears(2));
        mockMvc.perform(put("/cards/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void deleteCard_whenCardExists_returnsNoContent() throws Exception {
        Card savedCard = cardRepository.save(new Card(testUser, "1234", "Holder", LocalDate.now()));

        mockMvc.perform(delete("/cards/{id}", savedCard.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void deleteCard_whenCardNotFound_returnsNotFound() throws Exception {
        mockMvc.perform(delete("/cards/{id}", 999L))
                .andExpect(status().isNotFound());
    }
}
