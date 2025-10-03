package com.example.spring.controller;

import com.example.spring.dto.CreateOrderRequest;
import com.example.spring.dto.response.OrderResponse;
import com.example.spring.entity.Order;
import com.example.spring.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 주문 관리 REST API Controller (레거시)
 * 새로운 OrderController를 사용하세요
 * @deprecated Use {@link OrderController} instead
 */
@Deprecated
@RestController
@RequestMapping("/api/legacy")
@RequiredArgsConstructor
@Slf4j
public class BookstoreController {

    private final OrderService orderService;

    @PostMapping("/orders")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        log.info("주문 생성 요청: {}", request);

        try {
            Order order = orderService.createOrder(request.getBookIds());
            OrderResponse response = OrderResponse.from(order);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("주문 생성 실패", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<Page<OrderResponse>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderService.findAllOrders(pageable);
        Page<OrderResponse> response = orders.map(OrderResponse::from);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        try {
            Order order = orderService.findOrderById(id);
            OrderResponse response = OrderResponse.from(order);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}