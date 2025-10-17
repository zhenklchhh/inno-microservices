package com.innowise.orderservice.model;

import java.time.LocalDate;

/**
 * @author Evgeniy Zaleshchenok
 */
public record OrderDto (Long userId, String status, LocalDate creationDate){
}
