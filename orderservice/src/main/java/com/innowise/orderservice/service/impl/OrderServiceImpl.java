package com.innowise.orderservice.service.impl;

import com.innowise.orderservice.client.UserClient;
import com.innowise.orderservice.exception.InvalidRequestException;
import com.innowise.orderservice.exception.OrderNotFoundException;
import com.innowise.orderservice.exception.ServiceUnavailableException;
import com.innowise.orderservice.exception.UserNotFoundInUserServiceException;
import com.innowise.orderservice.mapper.OrderItemMapper;
import com.innowise.orderservice.mapper.OrderMapper;
import com.innowise.orderservice.model.*;
import com.innowise.orderservice.model.entity.Item;
import com.innowise.orderservice.model.entity.Order;
import com.innowise.orderservice.model.entity.OrderItem;
import com.innowise.orderservice.model.entity.OrderStatus;
import com.innowise.orderservice.repository.ItemRepository;
import com.innowise.orderservice.repository.OrderRepository;
import com.innowise.orderservice.security.TokenProvider;
import com.innowise.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Evgeniy Zaleshchenok
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserClient userClient;
    private final TokenProvider tokenProvider;
    private final OrderItemMapper orderItemMapper;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public OrderResponseDto createOrder(OrderDto orderDto, String token) {
        UserDto userDto = getUserData(token);
        Order order = orderMapper.toEntity(orderDto);
        List<Long> itemIds = orderDto.orderItems().stream()
                .map(OrderItemDto::itemId)
                .toList();
        List<Item> items = itemRepository.findAllById(itemIds);

        Map<Long, Item> itemMap = items.stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));

        for (int i = 0; i < order.getOrderItems().size(); i++) {
            OrderItem orderItem = order.getOrderItems().get(i);
            Long itemId = orderDto.orderItems().get(i).itemId();
            orderItem.setItem(itemMap.get(itemId));
            orderItem.setOrder(order);
        }
        order.setStatus(OrderStatus.CREATED);
        order.setCreationDate(LocalDate.now());
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toResponseDto(savedOrder, userDto);
    }

    @Transactional(readOnly = true)
    @Override
    public OrderResponseDto getOrder(Long orderId, String token) {
        UserDto userDto = getUserData(token);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        return orderMapper.toResponseDto(order, userDto);
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderResponseDto> getAllOrders(String token) {
        UserDto userDto = getUserData(token);
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(order -> orderMapper.toResponseDto(order, userDto))
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderResponseDto> getOrdersByIds(List<Long> orderIds, String token) {
        UserDto userDto = getUserData(token);
        List<Order> orders = orderRepository.findByIdIn(orderIds);
        return orders.stream()
                .map(order -> orderMapper.toResponseDto(order, userDto))
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderResponseDto> getOrdersByStatuses(List<OrderStatus> statuses, String token) {
        UserDto userDto = getUserData(token);
        List<Order> orders = orderRepository.findByStatusIn(statuses);
        return orders.stream()
                .map(order -> orderMapper.toResponseDto(order, userDto))
                .toList();
    }

    @Transactional
    @Override
    public OrderResponseDto updateOrder(Long id, OrderUpdateDto orderUpdateDto, String token) {
        UserDto userDto = getUserData(token);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        if (orderUpdateDto.getStatus() != null) {
            order.setStatus(orderUpdateDto.getStatus());
        }
        if (orderUpdateDto.getOrderItems() != null) {
            List<OrderItem> orderItems = orderUpdateDto.getOrderItems().stream()
                    .map(orderItemMapper::toEntity)
                    .toList();
            order.getOrderItems().clear();
            order.getOrderItems().addAll(orderItems);
        }
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toResponseDto(updatedOrder, userDto);
    }

    @Transactional
    @Override
    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }


    private UserDto getUserData(String token) {
        String email = tokenProvider.getEmailFromToken(token);
        UserDto userDto;
        try {
            userDto = userClient.getUserByEmail(email, token);
        } catch (UserNotFoundInUserServiceException e) {
            throw new InvalidRequestException(e.getMessage());
        }
        if (userDto.isPartial()) {
            throw new ServiceUnavailableException("User service is temporarily unavailable, cannot fetch user details");
        }
        return userDto;
    }
}
