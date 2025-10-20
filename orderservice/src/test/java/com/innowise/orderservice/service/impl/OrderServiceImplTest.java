package com.innowise.orderservice.service.impl;

import com.innowise.orderservice.exception.OrderNotFoundException;
import com.innowise.orderservice.mapper.OrderItemMapper;
import com.innowise.orderservice.mapper.OrderMapper;
import com.innowise.orderservice.model.OrderDto;
import com.innowise.orderservice.model.OrderResponseDto;
import com.innowise.orderservice.model.OrderUpdateDto;
import com.innowise.orderservice.model.UserDto;
import com.innowise.orderservice.model.entity.Order;
import com.innowise.orderservice.model.entity.OrderStatus;
import com.innowise.orderservice.repository.OrderRepository;
import com.innowise.orderservice.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderItemMapper orderItemMapper;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private WebClient.Builder webClientBuilder;
    @Mock
    private WebClient webClient;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private OrderServiceImpl orderServiceImpl;

    private Order testOrder;
    private OrderDto testOrderDto;
    private OrderUpdateDto testOrderUpdateDto;
    private UserDto testUserDto;
    private OrderResponseDto testOrderResponseDto;
    private String testToken;
    private String userServiceUrl = "http://test-user-service.com";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(orderServiceImpl, "userServiceUrl", userServiceUrl);

        testToken = "Bearer test.token";

        testUserDto = new UserDto();
        testUserDto.setId(1L);
        testUserDto.setEmail("test@example.com");

        testOrderDto = new OrderDto(1L, Collections.emptyList());

        testOrderUpdateDto = new OrderUpdateDto();
        testOrderUpdateDto.setStatus(OrderStatus.PROCESSING);

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setUserId(1L);
        testOrder.setStatus(OrderStatus.CREATED);
        testOrder.setCreationDate(LocalDate.now());

        testOrderResponseDto = new OrderResponseDto();
        testOrderResponseDto.setId(1L);
        testOrderResponseDto.setUser(testUserDto);
        testOrderResponseDto.setStatus(OrderStatus.CREATED.name());
    }

    private void mockWebClientChain() {
        when(jwtTokenProvider.getEmailFromToken(anyString())).thenReturn("test@example.com");
        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserDto.class)).thenReturn(Mono.just(testUserDto));
    }

    @Test
    void createOrder_shouldSaveAndReturnResponseDto() {
        when(orderMapper.toEntity(any(OrderDto.class))).thenReturn(testOrder);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        mockWebClientChain();
        when(orderMapper.toResponseDto(any(Order.class), any(UserDto.class))).thenReturn(testOrderResponseDto);

        OrderResponseDto result = orderServiceImpl.createOrder(testOrderDto, testToken);

        assertNotNull(result);
        assertEquals(testOrderResponseDto.getId(), result.getId());
        verify(orderRepository, times(1)).save(testOrder);
        verify(orderMapper, times(1)).toResponseDto(testOrder, testUserDto);
    }

    @Test
    void getOrder_whenOrderExists_shouldReturnResponseDto() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        mockWebClientChain();
        when(orderMapper.toResponseDto(any(Order.class), any(UserDto.class))).thenReturn(testOrderResponseDto);

        OrderResponseDto result = orderServiceImpl.getOrder(1L, testToken);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void getOrder_whenOrderDoesNotExist_shouldThrowOrderNotFoundException() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderServiceImpl.getOrder(99L, testToken));
        verify(webClientBuilder, never()).build();
    }

    @Test
    void getAllOrders_shouldReturnListOfResponseDtos() {
        List<Order> orders = List.of(testOrder);
        when(orderRepository.findAll()).thenReturn(orders);
        mockWebClientChain();
        when(orderMapper.toResponseDto(any(Order.class), any(UserDto.class))).thenReturn(testOrderResponseDto);

        List<OrderResponseDto> results = orderServiceImpl.getAllOrders(testToken);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(testOrderResponseDto.getId(), results.get(0).getId());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void getOrdersByIds_shouldReturnListOfResponseDtos() {
        List<Long> orderIds = List.of(1L);
        List<Order> orders = List.of(testOrder);
        when(orderRepository.findByIds(orderIds)).thenReturn(orders);
        mockWebClientChain();
        when(orderMapper.toResponseDto(any(Order.class), any(UserDto.class))).thenReturn(testOrderResponseDto);

        List<OrderResponseDto> results = orderServiceImpl.getOrdersByIds(orderIds, testToken);

        assertNotNull(results);
        assertEquals(1, results.size());
        verify(orderRepository, times(1)).findByIds(orderIds);
    }

    @Test
    void getOrdersByStatuses_shouldReturnListOfResponseDtos() {
        List<OrderStatus> statuses = List.of(OrderStatus.CREATED);
        List<Order> orders = List.of(testOrder);
        when(orderRepository.findByStatusIn(statuses)).thenReturn(orders);
        mockWebClientChain();
        when(orderMapper.toResponseDto(any(Order.class), any(UserDto.class))).thenReturn(testOrderResponseDto);

        List<OrderResponseDto> results = orderServiceImpl.getOrdersByStatuses(statuses, testToken);

        assertNotNull(results);
        assertEquals(1, results.size());
        verify(orderRepository, times(1)).findByStatusIn(statuses);
    }

    @Test
    void updateOrder_whenOrderExists_shouldUpdateAndReturnResponseDto() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        mockWebClientChain();
        when(orderMapper.toResponseDto(any(Order.class), any(UserDto.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            testOrderResponseDto.setStatus(savedOrder.getStatus().name());
            return testOrderResponseDto;
        });

        OrderResponseDto result = orderServiceImpl.updateOrder(1L, testOrderUpdateDto, testToken);

        assertNotNull(result);
        assertEquals(OrderStatus.PROCESSING.name(), result.getStatus());
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void updateOrder_whenOrderDoesNotExist_shouldThrowOrderNotFoundException() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderServiceImpl.updateOrder(99L, testOrderUpdateDto, testToken));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void deleteOrder_shouldInvokeRepositoryDelete() {
        doNothing().when(orderRepository).deleteById(1L);

        orderServiceImpl.deleteOrder(1L);

        verify(orderRepository, times(1)).deleteById(1L);
    }
}