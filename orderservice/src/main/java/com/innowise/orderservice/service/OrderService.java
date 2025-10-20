package com.innowise.orderservice.service;

import com.innowise.orderservice.exception.OrderNotFoundException;
import com.innowise.orderservice.model.OrderDto;
import com.innowise.orderservice.model.OrderResponseDto;
import com.innowise.orderservice.model.OrderUpdateDto;
import com.innowise.orderservice.model.entity.OrderStatus;

import java.util.List;

/**
 * Service interface for managing orders.
 * Defines the business logic for creating, retrieving, updating, and deleting orders.
 *
 * @author Evgeniy Zaleshchenok
 */
public interface OrderService {

    /**
     * Creates a new order based on the provided data.
     *
     * @param orderDto A DTO containing the data for the new order, such as user ID and items.
     * @param token    The authorization token of the user creating the order, used to fetch user details.
     * @return An {@link OrderResponseDto} representing the newly created order, enriched with user information.
     */
    OrderResponseDto createOrder(OrderDto orderDto, String token);

    /**
     * Retrieves a single order by its unique identifier.
     *
     * @param orderId The ID of the order to retrieve.
     * @param token   The authorization token of the user, used for fetching related user data.
     * @return An {@link OrderResponseDto} for the found order.
     * @throws OrderNotFoundException if no order with the specified ID is found.
     */
    OrderResponseDto getOrder(Long orderId, String token);

    /**
     * Retrieves a list of all orders.
     *
     * @param token The authorization token of the user.
     * @return A list of {@link OrderResponseDto} objects.
     */
    List<OrderResponseDto> getAllOrders(String token);

    /**
     * Retrieves a list of orders based on a list of their IDs.
     *
     * @param orderIds A list of order IDs to search for.
     * @param token    The authorization token of the user.
     * @return A list of {@link OrderResponseDto} objects matching the provided IDs.
     */
    List<OrderResponseDto> getOrdersByIds(List<Long> orderIds, String token);

    /**
     * Retrieves a list of orders filtered by one or more statuses.
     *
     * @param statuses A list of {@link OrderStatus} enums to filter by.
     * @param token    The authorization token of the user.
     * @return A list of {@link OrderResponseDto} objects that match any of the given statuses.
     */
    List<OrderResponseDto> getOrdersByStatuses(List<OrderStatus> statuses, String token);

    /**
     * Partially updates an existing order.
     * Allows for changing the order's status and/or its items.
     *
     * @param id             The ID of the order to update.
     * @param orderUpdateDto A DTO containing the fields to be updated. Fields left as null will not be changed.
     * @param token          The authorization token of the user performing the update.
     * @return An {@link OrderResponseDto} representing the state of the order after the update.
     * @throws OrderNotFoundException if no order with the specified ID is found.
     */
    OrderResponseDto updateOrder(Long id, OrderUpdateDto orderUpdateDto, String token);

    /**
     * Deletes an order by its unique identifier.
     *
     * @param orderId The ID of the order to be deleted.
     * @throws org.springframework.dao.EmptyResultDataAccessException if no order with the specified ID is found.
     */
    void deleteOrder(Long orderId);
}