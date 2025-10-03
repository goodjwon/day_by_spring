package com.example.spring.controller;

import com.example.spring.dto.CreateOrderRequest;
import com.example.spring.dto.response.OrderResponse;
import com.example.spring.entity.Order;
import com.example.spring.entity.OrderStatus;
import com.example.spring.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 주문 관리 REST API 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 주문 생성
     */
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        log.info("주문 생성 요청 - 도서 수: {}", request.getBookIds().size());

        Order order = orderService.createOrder(request.getBookIds());
        OrderResponse response = OrderResponse.from(order);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 주문 단건 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        log.debug("주문 조회 요청 - ID: {}", id);

        Order order = orderService.findOrderById(id);
        OrderResponse response = OrderResponse.from(order);

        return ResponseEntity.ok(response);
    }

    /**
     * 주문 목록 조회 (페이징)
     */
    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderDate") String sort,
            @RequestParam(defaultValue = "desc") String direction) {

        log.debug("주문 목록 조회 - page: {}, size: {}", page, size);

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<Order> orders = orderService.findAllOrders(pageable);
        Page<OrderResponse> response = orders.map(OrderResponse::from);

        return ResponseEntity.ok(response);
    }

    /**
     * 주문 상태 변경
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {

        log.info("주문 상태 변경 - ID: {}, 상태: {}", id, status);

        Order order = orderService.updateOrderStatus(id, status);
        OrderResponse response = OrderResponse.from(order);

        return ResponseEntity.ok(response);
    }

    /**
     * 주문 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody CreateOrderRequest request) {

        log.info("주문 수정 요청 - ID: {}", id);

        Order order = orderService.updateOrder(id, request.getBookIds());
        OrderResponse response = OrderResponse.from(order);

        return ResponseEntity.ok(response);
    }

    /**
     * 주문 취소
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long id) {
        log.info("주문 취소 요청 - ID: {}", id);

        Order order = orderService.cancelOrder(id);
        OrderResponse response = OrderResponse.from(order);

        return ResponseEntity.ok(response);
    }

    /**
     * 상태별 주문 조회
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<OrderResponse>> getOrdersByStatus(
            @PathVariable OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.debug("상태별 주문 조회 - 상태: {}", status);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "orderDate"));
        Page<Order> orders = orderService.findOrdersByStatus(status, pageable);
        Page<OrderResponse> response = orders.map(OrderResponse::from);

        return ResponseEntity.ok(response);
    }

    /**
     * 기간별 주문 조회
     */
    @GetMapping("/period")
    public ResponseEntity<Page<OrderResponse>> getOrdersByPeriod(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.debug("기간별 주문 조회 - 시작: {}, 종료: {}", startDate, endDate);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "orderDate"));
        Page<Order> orders = orderService.findOrdersByPeriod(startDate, endDate, pageable);
        Page<OrderResponse> response = orders.map(OrderResponse::from);

        return ResponseEntity.ok(response);
    }

    /**
     * 주문 통계 조회
     */
    @GetMapping("/statistics")
    public ResponseEntity<OrderStatistics> getOrderStatistics() {
        log.debug("주문 통계 조회");

        long totalOrders = orderService.getTotalOrdersCount();
        long pendingOrders = orderService.getOrdersCountByStatus(OrderStatus.PENDING);
        long confirmedOrders = orderService.getOrdersCountByStatus(OrderStatus.CONFIRMED);
        long shippedOrders = orderService.getOrdersCountByStatus(OrderStatus.SHIPPED);
        long deliveredOrders = orderService.getOrdersCountByStatus(OrderStatus.DELIVERED);
        long cancelledOrders = orderService.getOrdersCountByStatus(OrderStatus.CANCELLED);
        BigDecimal totalRevenue = orderService.getTotalRevenue();
        BigDecimal averageOrderAmount = orderService.getAverageOrderAmount();

        OrderStatistics statistics = OrderStatistics.builder()
                .totalOrders(totalOrders)
                .pendingOrders(pendingOrders)
                .confirmedOrders(confirmedOrders)
                .shippedOrders(shippedOrders)
                .deliveredOrders(deliveredOrders)
                .cancelledOrders(cancelledOrders)
                .totalRevenue(totalRevenue)
                .averageOrderAmount(averageOrderAmount)
                .build();

        return ResponseEntity.ok(statistics);
    }

    /**
     * 일별 주문 통계
     */
    @GetMapping("/statistics/daily")
    public ResponseEntity<List<DailyOrderStatistics>> getDailyOrderStatistics(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {

        log.debug("일별 주문 통계 조회 - 시작: {}, 종료: {}", startDate, endDate);

        List<DailyOrderStatistics> statistics = orderService.getDailyOrderStatistics(startDate, endDate);
        return ResponseEntity.ok(statistics);
    }

    /**
     * 상위 판매 도서 조회
     */
    @GetMapping("/statistics/top-books")
    public ResponseEntity<List<Map<String, Object>>> getTopSellingBooks(
            @RequestParam(defaultValue = "10") int limit) {

        log.debug("상위 판매 도서 조회 - 한도: {}", limit);

        List<Map<String, Object>> topBooks = orderService.getTopSellingBooks(limit);
        return ResponseEntity.ok(topBooks);
    }

    /**
     * 주문 통계 응답 DTO
     */
    @lombok.Getter
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class OrderStatistics {
        private long totalOrders;
        private long pendingOrders;
        private long confirmedOrders;
        private long shippedOrders;
        private long deliveredOrders;
        private long cancelledOrders;
        private BigDecimal totalRevenue;
        private BigDecimal averageOrderAmount;
    }

    /**
     * 일별 주문 통계 응답 DTO
     */
    @lombok.Getter
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class DailyOrderStatistics {
        private LocalDateTime date;
        private long orderCount;
        private BigDecimal totalAmount;
    }
}
