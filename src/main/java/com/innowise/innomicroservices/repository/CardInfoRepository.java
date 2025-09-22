package com.innowise.innomicroservices.repository;

import com.innowise.innomicroservices.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Evgeniy Zaleshchenok
 */
public interface CardInfoRepository extends JpaRepository<Card, Long> {
    @Query(value = "SELECT c.* FROM card_info c WHERE c.id in :cardIds", nativeQuery = true)
    List<Card> findCardsByIds(@Param("cardIds") List<Long> cardIds);

    @Modifying
    @Query("UPDATE Card c SET c.holder = :holder WHERE c.id = :id")
    void updateHolderById(@Param("id") Long id, @Param("holder") String holder);
}
