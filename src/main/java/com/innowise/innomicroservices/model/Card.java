package com.innowise.innomicroservices.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * @author Evgeniy Zaleshchenok
 */
@Entity
@Table(name="card_info")
@Getter
@Setter
public class Card {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;
    @Column(name="number")
    private String cardNumber;
    private String holder;
    @Column(name="expiration_date")
    private LocalDate expiryDate;
}
