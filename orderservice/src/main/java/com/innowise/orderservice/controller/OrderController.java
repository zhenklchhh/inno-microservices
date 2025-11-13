package com.innowise.orderservice.controller;

import com.innowise.orderservice.model.OrderDto;
import com.innowise.orderservice.model.OrderResponseDto;
import com.innowise.orderservice.model.OrderUpdateDto;
import com.innowise.orderservice.model.entity.OrderStatus;
import com.innowise.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Evgeniy Zaleshchenok
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@Valid @RequestBody OrderDto orderDto,
                                                        @RequestHeader("Authorization") String authHeader) throws BadRequestException {
        OrderResponseDto order = orderService.createOrder(orderDto, authHeader);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getAllOrders(@RequestHeader("Authorization") String authHeader) throws BadRequestException {
        return ResponseEntity.ok(orderService.getAllOrders(authHeader));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long id,
                                                         @RequestHeader("Authorization") String authHeader) throws BadRequestException {
        return ResponseEntity.ok(orderService.getOrder(id, authHeader));
    }

    @GetMapping(params = "ids")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByIds(@RequestParam List<Long> ids,
                                                                 @RequestHeader("Authorization") String authHeader) throws BadRequestException {
        return ResponseEntity.ok(orderService.getOrdersByIds(ids, authHeader));
    }

    @GetMapping(params = "statuses")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByStatuses(@RequestParam List<OrderStatus> statuses,
                                                                      @RequestHeader("Authorization") String authHeader) throws BadRequestException {
        return ResponseEntity.ok(orderService.getOrdersByStatuses(statuses, authHeader));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OrderResponseDto> updateOrder(@PathVariable Long id,
                                                        @Valid @RequestBody OrderUpdateDto orderDto,
                                                        @RequestHeader("Authorization") String authHeader) throws BadRequestException {
        return ResponseEntity.ok(orderService.updateOrder(id, orderDto, authHeader));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
