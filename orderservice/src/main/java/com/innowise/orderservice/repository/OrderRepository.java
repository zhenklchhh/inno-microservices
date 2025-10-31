package com.innowise.orderservice.repository;

import com.innowise.orderservice.model.entity.Order;
import com.innowise.orderservice.model.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Evgeniy Zaleshchenok
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByIdIn(List<Long> orderIds);
    List<Order> findByStatusIn(List<OrderStatus> statuses);
}
