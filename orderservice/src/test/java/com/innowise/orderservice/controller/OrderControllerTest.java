package com.innowise.orderservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.orderservice.exception.OrderNotFoundException;
import com.innowise.orderservice.model.*;
import com.innowise.orderservice.model.entity.OrderStatus;
import com.innowise.orderservice.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OrderController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@Import(GlobalExceptionHandler.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    private OrderDto orderDto;
    private OrderUpdateDto orderUpdateDto;
    private OrderResponseDto orderResponseDto;
    private String testAuthHeader;

    @BeforeEach
    void setUp() {
        testAuthHeader = "Bearer test.token.jwt";

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("test@example.com");

        OrderItemDto itemDto = new OrderItemDto(1L, 1);
        orderDto = new OrderDto(1L, List.of(itemDto));

        orderUpdateDto = new OrderUpdateDto();
        orderUpdateDto.setStatus(OrderStatus.PROCESSING);

        orderResponseDto = new OrderResponseDto();
        orderResponseDto.setId(1L);
        orderResponseDto.setUser(userDto);
        orderResponseDto.setStatus(OrderStatus.CREATED.name());
        orderResponseDto.setCreationDate(LocalDate.now());
        orderResponseDto.setOrderItems(List.of(itemDto));
    }

    @Test
    void createOrder_shouldReturnCreated() throws Exception {
        when(orderService.createOrder(any(OrderDto.class), anyString())).thenReturn(orderResponseDto);

        mockMvc.perform(post("/orders")
                        .header("Authorization", testAuthHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.user.email").value("test@example.com"));
    }

    @Test
    void getAllOrders_shouldReturnOk() throws Exception {
        when(orderService.getAllOrders(anyString())).thenReturn(List.of(orderResponseDto));

        mockMvc.perform(get("/orders")
                        .header("Authorization", testAuthHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getOrderById_shouldReturnOk() throws Exception {
        when(orderService.getOrder(anyLong(), anyString())).thenReturn(orderResponseDto);

        mockMvc.perform(get("/orders/{id}", 1L)
                        .header("Authorization", testAuthHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getOrdersByIds_shouldReturnOk() throws Exception {
        when(orderService.getOrdersByIds(any(), anyString())).thenReturn(List.of(orderResponseDto));

        mockMvc.perform(get("/orders")
                        .param("ids", "1", "2")
                        .header("Authorization", testAuthHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getOrdersByStatuses_shouldReturnOk() throws Exception {
        when(orderService.getOrdersByStatuses(any(), anyString())).thenReturn(List.of(orderResponseDto));

        mockMvc.perform(get("/orders")
                        .param("statuses", "CREATED", "PROCESSING")
                        .header("Authorization", testAuthHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void updateOrder_shouldReturnOk() throws Exception {
        orderResponseDto.setStatus(OrderStatus.PROCESSING.name());
        when(orderService.updateOrder(anyLong(), any(OrderUpdateDto.class), anyString())).thenReturn(orderResponseDto);

        mockMvc.perform(patch("/orders/{id}", 1L)
                        .header("Authorization", testAuthHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("PROCESSING"));
    }

    @Test
    void deleteOrder_shouldReturnNoContent() throws Exception {
        doNothing().when(orderService).deleteOrder(anyLong());

        mockMvc.perform(delete("/orders/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void createOrder_whenBodyIsInvalid_shouldReturnBadRequest() throws Exception {
        OrderDto invalidOrderDto = new OrderDto(null, Collections.emptyList());

        mockMvc.perform(post("/orders")
                        .header("Authorization", testAuthHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidOrderDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOrderById_whenOrderNotFound_shouldReturnNotFound() throws Exception {
        long nonExistentId = 99L;
        when(orderService.getOrder(nonExistentId, testAuthHeader))
                .thenThrow(new OrderNotFoundException(nonExistentId));

        mockMvc.perform(get("/orders/{id}", nonExistentId)
                        .header("Authorization", testAuthHeader))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order with id " + nonExistentId + " not found"));
    }

    @Test
    void createOrder_whenAuthHeaderIsMissing_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOrderById_withInvalidIdType_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/orders/{id}", "abc")
                        .header("Authorization", testAuthHeader))
                .andExpect(status().isBadRequest());
    }
}