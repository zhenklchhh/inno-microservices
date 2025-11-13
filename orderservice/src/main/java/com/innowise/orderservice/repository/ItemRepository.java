package com.innowise.orderservice.repository;

import com.innowise.orderservice.model.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Evgeniy Zaleshchenok
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByIdIn(List<Long> ids);
}
