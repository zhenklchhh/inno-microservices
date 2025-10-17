package com.innowise.orderservice.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Evgeniy Zaleshchenok
 */
@Table(name= "orders")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="user_id")
    private Long userId;
    private String status;
    @Column(name="creation_date")
    private LocalDate creationDate;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems;
}
