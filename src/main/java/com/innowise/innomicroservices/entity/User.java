package com.innowise.innomicroservices.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Evgeniy Zaleshchenok
 */
@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;
    private String name;
    private String surname;
    private LocalDate birthDate;
    private String email;
    @OneToMany(mappedBy="users", cascade = CascadeType.ALL)
    private List<Card> cards = new ArrayList<>();
}
