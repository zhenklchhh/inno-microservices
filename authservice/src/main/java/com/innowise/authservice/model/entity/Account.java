package com.innowise.authservice.model.entity;

import com.innowise.authservice.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

/**
 * @author Evgeniy Zaleshchenok
 */
@Table(name = "accounts")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long id;

    @Column(nullable = false, unique = true)
    private String login;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;
}
