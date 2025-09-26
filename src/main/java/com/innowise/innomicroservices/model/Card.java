package com.innowise.innomicroservices.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

/**
 * @author Evgeniy Zaleshchenok
 */
@Entity
@Table(name="card_info")
@Getter
@Setter
public class Card {
    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;
    private String cardNumber;
    private String holder;
    private LocalDate expiryDate;
}
