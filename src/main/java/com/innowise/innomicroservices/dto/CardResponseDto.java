package com.innowise.innomicroservices.dto;

import java.time.LocalDate;

/**
 * @author Evgeniy Zaleshchenok
 */
public record CardResponseDto(Long id, Long userId, String cardNumber, String holder, LocalDate expiryDate) {
}
