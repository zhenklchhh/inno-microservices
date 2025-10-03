package com.innowise.innomicroservices.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * @author Evgeniy Zaleshchenok
 */
@Entity
@Table(name="card_info")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

    public Card(User user, String cardNumber, String holder, LocalDate expiryDate) {
        this.user = user;
        this.cardNumber = cardNumber;
        this.holder = holder;
        this.expiryDate = expiryDate;
    }
}
