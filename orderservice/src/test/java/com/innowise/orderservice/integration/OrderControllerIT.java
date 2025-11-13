package com.innowise.orderservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.innowise.orderservice.config.FakeJwtTokenProvider;
import com.innowise.orderservice.model.OrderDto;
import com.innowise.orderservice.model.OrderItemDto;
import com.innowise.orderservice.model.OrderUpdateDto;
import com.innowise.orderservice.model.UserDto;
import com.innowise.orderservice.model.entity.Item;
import com.innowise.orderservice.model.entity.Order;
import com.innowise.orderservice.model.entity.OrderItem;
import com.innowise.orderservice.model.entity.OrderStatus;
import com.innowise.orderservice.repository.ItemRepository;
import com.innowise.orderservice.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Evgeniy Zaleshchenok
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ContextConfiguration(classes = {OrderControllerIT.TestConfig.class})
@WithMockUser(username = "test@example.com")
public class OrderControllerIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    static WireMockServer wireMockServer;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private FakeJwtTokenProvider fakeJwtTokenProvider;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    private Item testItem;
    private Order testOrder;
    private UserDto mockUserResponse;
    private String authToken;
    private final String userEmail = "test@example.com";

    @TestConfiguration
    @ComponentScan("com.innowise.orderservice.config")
    static class TestConfig {}

    @BeforeAll
    static void startWireMockServer() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
    }

    @AfterAll
    static void stopWireMockServer() {
        wireMockServer.stop();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("user-service.url", wireMockServer::baseUrl);
    }

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        itemRepository.deleteAll();

        Item itemToSave = new Item();
        itemToSave.setName("Test Laptop");
        itemToSave.setPrice(1500L);
        testItem = itemRepository.save(itemToSave);

        testOrder = new Order();
        testOrder.setUserId(1L);
        testOrder.setStatus(OrderStatus.CREATED);
        testOrder.setCreationDate(LocalDate.now());

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(testOrder);
        orderItem.setItem(testItem);
        orderItem.setQuantity(2);

        testOrder.setOrderItems(List.of(orderItem));
        orderRepository.save(testOrder);

        authToken = "Bearer test.token.jwt";
        fakeJwtTokenProvider.setTestUserEmail(userEmail);

        mockUserResponse = new UserDto(1L, "John", "Doe",
                LocalDate.parse("2005-02-02"), userEmail, false);
    }

    @AfterEach
    void reset() {
        wireMockServer.resetAll();
        circuitBreakerRegistry.circuitBreaker("userService").reset();
    }

    @Test
    void createOrder_whenUserExists_shouldReturnCreated() throws Exception {
        stubFor(WireMock.get(urlPathEqualTo("/users"))
                .withQueryParam("email", equalTo(userEmail))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(mockUserResponse))));

        OrderDto requestOrderDto = new OrderDto(mockUserResponse.getId(), List.of(new OrderItemDto(testItem.getId(), 5)));

        mockMvc.perform(post("/orders")
                        .with(csrf())
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestOrderDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.id").value(mockUserResponse.getId()))
                .andExpect(jsonPath("$.orderItems[0].itemId").value(testItem.getId()))
                .andExpect(jsonPath("$.orderItems[0].quantity").value(5));

        assertThat(orderRepository.count()).isEqualTo(2);
    }

    @Test
    void createOrder_whenUserServiceIsNotResponding_shouldReturnServiceUnavailable() throws Exception {
        OrderDto requestOrderDto = new OrderDto(1L, List.of(new OrderItemDto(testItem.getId(), 1)));
        circuitBreakerRegistry.circuitBreaker("userService").transitionToOpenState();

        mockMvc.perform(post("/orders")
                        .with(csrf())
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestOrderDto)))
                .andExpect(status().isServiceUnavailable());

        assertThat(orderRepository.count()).isEqualTo(1);
    }

    @Test
    void createOrder_whenUserNotExist_shouldReturnBadRequest() throws Exception {
        stubFor(WireMock.get(urlPathEqualTo("/users"))
                .withQueryParam("email", equalTo(userEmail))
                .willReturn(aResponse().withStatus(HttpStatus.NOT_FOUND.value())));

        OrderDto requestOrderDto = new OrderDto(999L, List.of(new OrderItemDto(testItem.getId(), 1)));

        mockMvc.perform(post("/orders")
                        .with(csrf())
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestOrderDto)))
                .andExpect(status().isBadRequest());

        assertThat(orderRepository.count()).isEqualTo(1);
    }

    @Test
    void getAllOrders_shouldReturnOk() throws Exception {
        stubFor(WireMock.get(urlPathEqualTo("/users"))
                .withQueryParam("email", equalTo(userEmail))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(mockUserResponse))));

        mockMvc.perform(get("/orders")
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(testOrder.getId()))
                .andExpect(jsonPath("$[0].user.id").value(mockUserResponse.getId()))
                .andExpect(jsonPath("$[0].orderItems[0].itemId").value(testItem.getId()));
    }

    @Test
    void getOrderById_whenOrderExists_shouldReturnOk() throws Exception {
        stubFor(WireMock.get(urlPathEqualTo("/users"))
                .withQueryParam("email", equalTo(userEmail))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(mockUserResponse))));

        mockMvc.perform(get("/orders/" + testOrder.getId())
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testOrder.getId()))
                .andExpect(jsonPath("$.status").value(OrderStatus.CREATED.toString()))
                .andExpect(jsonPath("$.user.email").value(userEmail))
                .andExpect(jsonPath("$.orderItems", hasSize(1)))
                .andExpect(jsonPath("$.orderItems[0].itemId").value(testItem.getId()))
                .andExpect(jsonPath("$.orderItems[0].quantity").value(2));
    }

    @Test
    void getOrdersByIds_whenOrdersExist_shouldReturnMatchingOrders() throws Exception {
        // Arrange
        Order anotherOrder = new Order();
        anotherOrder.setUserId(2L);
        anotherOrder.setStatus(OrderStatus.PROCESSING);
        orderRepository.save(anotherOrder);

        assertThat(orderRepository.count()).isEqualTo(2);

        stubFor(WireMock.get(urlPathEqualTo("/users"))
                .withQueryParam("email", equalTo(userEmail))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(mockUserResponse))));

        String requestedIds = testOrder.getId() + "," + anotherOrder.getId();

        // Act & Assert
        mockMvc.perform(get("/orders")
                        .param("ids", requestedIds)
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(testOrder.getId()))
                .andExpect(jsonPath("$[1].id").value(anotherOrder.getId()));
    }

    @Test
    void getOrdersByStatuses_whenOrdersExist_shouldReturnMatchingOrders() throws Exception {
        Order anotherCreatedOrder = new Order();
        anotherCreatedOrder.setUserId(1L);
        anotherCreatedOrder.setStatus(OrderStatus.CREATED);
        orderRepository.save(anotherCreatedOrder);

        Order paidOrder = new Order();
        paidOrder.setUserId(2L);
        paidOrder.setStatus(OrderStatus.PROCESSING);
        orderRepository.save(paidOrder);

        assertThat(orderRepository.count()).isEqualTo(3);

        stubFor(WireMock.get(urlPathEqualTo("/users"))
                .withQueryParam("email", equalTo(userEmail))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(mockUserResponse))));

        mockMvc.perform(get("/orders")
                        .param("statuses", OrderStatus.CREATED.toString())
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].status").value("CREATED"))
                .andExpect(jsonPath("$[1].status").value("CREATED"));
    }

    @Test
    void updateOrder_whenOrderExists_shouldUpdateAndReturnOk() throws Exception {
        // Arrange
        OrderUpdateDto updateDto = new OrderUpdateDto();
        updateDto.setStatus(OrderStatus.PROCESSING);

        stubFor(WireMock.get(urlPathEqualTo("/users"))
                .withQueryParam("email", equalTo(userEmail))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(mockUserResponse))));

        // Act & Assert
        mockMvc.perform(patch("/orders/" + testOrder.getId())
                        .with(csrf())
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testOrder.getId()))
                .andExpect(jsonPath("$.status").value(OrderStatus.PROCESSING.toString()));

        Order updatedOrderFromDb = orderRepository.findById(testOrder.getId()).orElseThrow();
        assertThat(updatedOrderFromDb.getStatus()).isEqualTo(OrderStatus.PROCESSING);
    }

    @Test
    void deleteOrder_whenOrderExists_shouldDeleteAndReturnNoContent() throws Exception {
        // Arrange
        assertThat(orderRepository.existsById(testOrder.getId())).isTrue();
        long initialCount = orderRepository.count();

        // Act & Assert
        mockMvc.perform(delete("/orders/" + testOrder.getId())
                        .with(csrf())
                        .header("Authorization", authToken))
                .andExpect(status().isNoContent());

        assertThat(orderRepository.existsById(testOrder.getId())).isFalse();
        assertThat(orderRepository.count()).isEqualTo(initialCount - 1);
    }

}

