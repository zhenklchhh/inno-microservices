package com.innowise.orderservice.controller;

import com.innowise.orderservice.exception.OrderNotFoundException;
import com.innowise.orderservice.model.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Evgeniy Zaleshchenok
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ApiError> handleOrderNotFoundException(OrderNotFoundException e, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Resource Not Found",
                e.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {

        Map<String, String> validationErrors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });

        ApiError apiError = new ApiError(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                validationErrors,
                request.getRequestURI()
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }
}
