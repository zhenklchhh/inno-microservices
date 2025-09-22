package com.innowise.innomicroservices.entity;

import jakarta.persistence.*;

/**
 * @author Evgeniy Zaleshchenok
 */
@Entity
@Table(name="card_info")
public class Card {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
    private String cardNumber;
    private String holder;
    private String expiryDate;
}
