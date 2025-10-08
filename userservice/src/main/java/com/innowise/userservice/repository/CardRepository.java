package com.innowise.userservice.repository;

import com.innowise.userservice.model.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Evgeniy Zaleshchenok
 */
public interface CardRepository extends JpaRepository<Card, Long> {
    @Query(value = "SELECT c.* FROM card_info c WHERE c.id in :cardIds", nativeQuery = true)
    List<Card> findCardsByIds(@Param("cardIds") List<Long> cardIds);
}
