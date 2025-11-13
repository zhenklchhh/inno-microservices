package com.innowise.orderservice.service.impl;

import com.innowise.orderservice.client.UserClient;
import com.innowise.orderservice.exception.OrderNotFoundException;
import com.innowise.orderservice.mapper.OrderItemMapper;
import com.innowise.orderservice.mapper.OrderMapper;
import com.innowise.orderservice.model.OrderDto;
import com.innowise.orderservice.model.OrderResponseDto;
import com.innowise.orderservice.model.OrderUpdateDto;
import com.innowise.orderservice.model.UserDto;
import com.innowise.orderservice.model.entity.Item;
import com.innowise.orderservice.model.entity.Order;
import com.innowise.orderservice.model.entity.OrderItem;
import com.innowise.orderservice.model.entity.OrderStatus;
import com.innowise.orderservice.repository.ItemRepository;
import com.innowise.orderservice.repository.OrderRepository;
import com.innowise.orderservice.security.TokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
    private ItemRepository itemRepository;
    @Mock
    private UserClient userClient;
    @Mock
    private TokenProvider tokenProvider;

    @InjectMocks
    private OrderServiceImpl orderServiceImpl;

    private Order testOrder;
    private OrderDto testOrderDto;
    private OrderUpdateDto testOrderUpdateDto;
    private UserDto testUserDto;
    private OrderResponseDto testOrderResponseDto;
    private String testToken;
    private String testUserEmail;

    @BeforeEach
    void setUp() {
        testToken = "Bearer test.token.jwt";
        testUserEmail = "test@example.com";

        testUserDto = new UserDto();
        testUserDto.setId(1L);
        testUserDto.setEmail(testUserEmail);
        testUserDto.setPartial(false);

        testOrderDto = new OrderDto(1L, List.of(new com.innowise.orderservice.model.OrderItemDto(1L, 2)));

        testOrderUpdateDto = new OrderUpdateDto();
        testOrderUpdateDto.setStatus(OrderStatus.PROCESSING);

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setUserId(1L);
        testOrder.setStatus(OrderStatus.CREATED);
        testOrder.setCreationDate(LocalDate.now());
        testOrder.setOrderItems(List.of(new OrderItem()));

        testOrderResponseDto = new OrderResponseDto();
        testOrderResponseDto.setId(1L);
        testOrderResponseDto.setUser(testUserDto);
        testOrderResponseDto.setStatus(OrderStatus.CREATED.name());
    }

    private void mockUserData() {
        when(tokenProvider.getEmailFromToken(anyString())).thenReturn(testUserEmail);
        when(userClient.getUserByEmail(eq(testUserEmail), anyString())).thenReturn(testUserDto);
    }

    @Test
    void createOrder_shouldSaveAndReturnResponseDto() {
        mockUserData();
        when(orderMapper.toEntity(any(OrderDto.class))).thenReturn(testOrder);
        when(itemRepository.findAllById(anyList())).thenReturn(List.of(new Item()));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(orderMapper.toResponseDto(any(Order.class), any(UserDto.class))).thenReturn(testOrderResponseDto);

        OrderResponseDto result = orderServiceImpl.createOrder(testOrderDto, testToken);

        assertNotNull(result);
        assertEquals(testOrderResponseDto.getId(), result.getId());
        verify(orderRepository).save(testOrder);
        verify(orderMapper).toResponseDto(testOrder, testUserDto);
    }

    @Test
    void getOrder_whenOrderExists_shouldReturnResponseDto() {
        mockUserData();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderMapper.toResponseDto(any(Order.class), any(UserDto.class))).thenReturn(testOrderResponseDto);

        OrderResponseDto result = orderServiceImpl.getOrder(1L, testToken);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(orderRepository).findById(1L);
    }

    @Test
    void getOrder_whenOrderDoesNotExist_shouldThrowOrderNotFoundException() {
        mockUserData();
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderServiceImpl.getOrder(99L, testToken));
    }

    @Test
    void getAllOrders_shouldReturnListOfResponseDtos() {
        mockUserData();
        when(orderRepository.findAll()).thenReturn(List.of(testOrder));
        when(orderMapper.toResponseDto(any(Order.class), any(UserDto.class))).thenReturn(testOrderResponseDto);

        List<OrderResponseDto> results = orderServiceImpl.getAllOrders(testToken);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(testOrderResponseDto.getId(), results.get(0).getId());
    }

    @Test
    void getOrdersByIds_shouldReturnListOfResponseDtos() {
        mockUserData();
        List<Long> orderIds = List.of(1L);
        when(orderRepository.findByIdIn(orderIds)).thenReturn(List.of(testOrder));
        when(orderMapper.toResponseDto(any(Order.class), any(UserDto.class))).thenReturn(testOrderResponseDto);

        List<OrderResponseDto> results = orderServiceImpl.getOrdersByIds(orderIds, testToken);

        assertNotNull(results);
        assertEquals(1, results.size());
    }

    @Test
    void getOrdersByStatuses_shouldReturnListOfResponseDtos() {
        mockUserData();
        List<OrderStatus> statuses = List.of(OrderStatus.CREATED);
        when(orderRepository.findByStatusIn(statuses)).thenReturn(List.of(testOrder));
        when(orderMapper.toResponseDto(any(Order.class), any(UserDto.class))).thenReturn(testOrderResponseDto);

        List<OrderResponseDto> results = orderServiceImpl.getOrdersByStatuses(statuses, testToken);

        assertNotNull(results);
        assertEquals(1, results.size());
    }

    @Test
    void updateOrder_whenOrderExists_shouldUpdateAndReturnResponseDto() {
        mockUserData();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderMapper.toResponseDto(any(Order.class), any(UserDto.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            testOrderResponseDto.setStatus(savedOrder.getStatus().name());
            return testOrderResponseDto;
        });

        OrderResponseDto result = orderServiceImpl.updateOrder(1L, testOrderUpdateDto, testToken);

        assertNotNull(result);
        assertEquals(OrderStatus.PROCESSING.name(), result.getStatus());
    }

    @Test
    void updateOrder_whenOrderDoesNotExist_shouldThrowOrderNotFoundException() {
        mockUserData();
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderServiceImpl.updateOrder(99L, testOrderUpdateDto, testToken));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void deleteOrder_shouldInvokeRepositoryDelete() {
        orderServiceImpl.deleteOrder(1L);

        verify(orderRepository, times(1)).deleteById(1L);
    }
}