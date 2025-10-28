package com.innowise.orderservice.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author Evgeniy Zaleshchenok
 */
@Data
public class UserDto {
    private Long id;
    private String name;
    private String surname;
    private LocalDate birthDate;
    private String email;
    private boolean partial;
}