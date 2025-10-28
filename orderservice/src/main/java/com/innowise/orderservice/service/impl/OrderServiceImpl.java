package com.innowise.orderservice.service.impl;

import com.innowise.orderservice.client.UserClient;
import com.innowise.orderservice.exception.OrderNotFoundException;
import com.innowise.orderservice.mapper.OrderItemMapper;
import com.innowise.orderservice.mapper.OrderMapper;
import com.innowise.orderservice.model.OrderDto;
import com.innowise.orderservice.model.OrderResponseDto;
import com.innowise.orderservice.model.OrderUpdateDto;
import com.innowise.orderservice.model.UserDto;
import com.innowise.orderservice.model.entity.Order;
import com.innowise.orderservice.model.entity.OrderItem;
import com.innowise.orderservice.model.entity.OrderStatus;
import com.innowise.orderservice.repository.OrderRepository;
import com.innowise.orderservice.security.JwtTokenProvider;
import com.innowise.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Evgeniy Zaleshchenok
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserClient userClient;
    private final JwtTokenProvider jwtTokenProvider;
    private final OrderItemMapper orderItemMapper;

    @Value("${user-service.url}")
    private String userServiceUrl;

    @Transactional
    @Override
    public OrderResponseDto createOrder(OrderDto orderDto, String token) {
        Order order = orderMapper.toEntity(orderDto);
        order.setStatus(OrderStatus.CREATED);
        order.setCreationDate(LocalDate.now());
        Order savedOrder = orderRepository.save(order);
        UserDto userDto = getUserData(token);
        return orderMapper.toResponseDto(savedOrder, userDto);
    }

    @Override
    public OrderResponseDto getOrder(Long orderId, String token) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        UserDto userDto = getUserData(token);
        return orderMapper.toResponseDto(order, userDto);
    }

    @Override
    public List<OrderResponseDto> getAllOrders(String token) {
        List<Order> orders = orderRepository.findAll();
        UserDto userDto = getUserData(token);
        return orders.stream()
                .map(order -> orderMapper.toResponseDto(order, userDto))
                .toList();
    }

    @Override
    public List<OrderResponseDto> getOrdersByIds(List<Long> orderIds, String token) {
        List<Order> orders = orderRepository.findByIds(orderIds);
        UserDto userDto = getUserData(token);
        return orders.stream()
                .map(order -> orderMapper.toResponseDto(order, userDto))
                .toList();
    }

    @Override
    public List<OrderResponseDto> getOrdersByStatuses(List<OrderStatus> statuses, String token) {
        List<Order> orders = orderRepository.findByStatusIn(statuses);
        UserDto userDto = getUserData(token);
        return orders.stream()
                .map(order -> orderMapper.toResponseDto(order, userDto))
                .toList();
    }

    @Transactional
    @Override
    public OrderResponseDto updateOrder(Long id, OrderUpdateDto orderUpdateDto, String token) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        if (orderUpdateDto.getStatus() != null){
            order.setStatus(orderUpdateDto.getStatus());
        }
        if(orderUpdateDto.getOrderItems() != null){
            List<OrderItem> orderItems = orderUpdateDto.getOrderItems().stream()
                .map(orderItemMapper::toEntity)
                .toList();
            order.getOrderItems().clear();
            order.getOrderItems().addAll(orderItems);
        }
        UserDto userDto = getUserData(token);
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toResponseDto(updatedOrder, userDto);
    }

    @Transactional
    @Override
    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }


    private UserDto getUserData(String token){
        String email = jwtTokenProvider.getEmailFromToken(token);
        return userClient.getUserByEmail(email, token);
    }
}
