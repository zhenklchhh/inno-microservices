package com.innowise.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.userservice.model.UserDto;
import com.innowise.userservice.model.entity.User;
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
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Container
    static PostgreSQLContainer<?> provider = new PostgreSQLContainer<>("postgres:13-alpine");
    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:6-alpine")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", provider::getJdbcUrl);
        registry.add("spring.datasource.username", provider::getUsername);
        registry.add("spring.datasource.password", provider::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379).toString());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void createUser_withValidData_returnsCreated() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("John");
        userDto.setSurname("Doe");
        userDto.setEmail("john.doe@example.com");
        userDto.setBirthDate(LocalDate.parse("2000-01-01"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("John"));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void createUser_withInvalidEmail_returnsBadRequest() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("Invalid");
        userDto.setEmail("not-an-email");
        userDto.setSurname("User");
        userDto.setBirthDate(LocalDate.parse("2000-01-01"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void getUserById_whenUserExists_returnsUser() throws Exception {
        User userToSave = new User("Jane", "Doe", LocalDate.parse("2003-06-23"), "jane.doe@example.com");

        User savedUser = userRepository.save(userToSave);

        mockMvc.perform(get("/users/{id}", savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.name").value("Jane"));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void getUserById_whenUserNotFound_returnsNotFound() throws Exception {
        mockMvc.perform(get("/users/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsers_whenUsersExist_returnsUserList() throws Exception {
        userRepository.saveAll(List.of(
                new User("User1", "Test", LocalDate.now(), "user1@test.com"),
                new User("User2", "Test", LocalDate.now(), "user2@test.com")
        ));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("User1"));
    }

    @Test
    void getAllUsers_whenNoUsers_returnsEmptyList() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getUsersByIds_whenUsersExist_returnsUserList() throws Exception {
        User user1 = userRepository.save(new User("User1", "Test", LocalDate.now(), "user1@test.com"));
        User user2 = userRepository.save(new User("User2", "Test", LocalDate.now(), "user2@test.com"));

        mockMvc.perform(
                        get("/users")
                                .param("ids", String.valueOf(user1.getId()), String.valueOf(user2.getId()))
                )
                .andExpect(status().isOk()) // А теперь проверяем ответ
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getUserByEmail_whenUserExists_returnsUser() throws Exception {
        User user = userRepository.save(new User("EmailUser", "Test", LocalDate.now(), "search@test.com"));

        mockMvc.perform(get("/users")
                        .param("email", "search@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()));
    }

    @Test
    void getUserByEmail_whenUserNotFound_returnsNotFound() throws Exception {
        mockMvc.perform(get("/users")
                        .param("email", "notfound@test.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void updateUser_whenUserExists_returnsUpdatedUser() throws Exception {
        User user = userRepository.save(new User("ToUpdate", "Test", LocalDate.now(), "update@test.com"));
        UserDto updateDto = new UserDto();
        updateDto.setName("UpdatedName");
        updateDto.setSurname("UpdatedSurname");
        updateDto.setEmail("updated@test.com");
        updateDto.setBirthDate(LocalDate.parse("1999-01-01"));

        mockMvc.perform(put("/users/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("UpdatedName"))
                .andExpect(jsonPath("$.email").value("updated@test.com"));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void updateUser_whenUserNotFound_returnsNotFound() throws Exception {
        UserDto updateDto = new UserDto();
        updateDto.setName("WontUpdate");
        updateDto.setSurname("WontUpdate");
        updateDto.setEmail("wontupdate@test.com");
        updateDto.setBirthDate(LocalDate.parse("1999-01-01"));


        mockMvc.perform(put("/users/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void deleteUser_whenUserExists_returnsNoContent() throws Exception {
        User user = userRepository.save(new User("ToDelete", "Test", LocalDate.now(), "delete@test.com"));

        mockMvc.perform(delete("/users/{id}", user.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void deleteUser_whenUserNotFound_returnsNotFound() throws Exception {
        mockMvc.perform(delete("/users/{id}", 999L))
                .andExpect(status().isNotFound());
    }
}