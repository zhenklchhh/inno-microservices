package com.innowise.orderservice.repository;

import com.innowise.orderservice.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Evgeniy Zaleshchenok
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByIds(List<Long> orderIds);
    List<Order> findByStatus(String status);
}
