package com.example.spring.controller;

import com.example.spring.dto.CreateOrderRequest;
import com.example.spring.entity.Order;
import com.example.spring.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 주문 관리 REST API Controller
 * 도서 관리는 BookController에서 담당
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class BookstoreController {

    private final OrderService orderService;

    @PostMapping("/orders")
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest request) {
        log.info("주문 생성 요청: {}", request);

        try {
            Order order = orderService.createOrder(request.getBookIds());
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            log.error("주문 생성 실패", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.findAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        try {
            Order order = orderService.findOrderById(id);
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}