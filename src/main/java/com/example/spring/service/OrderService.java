package com.example.spring.service;

import com.example.spring.controller.OrderController;
import com.example.spring.entity.Order;
import com.example.spring.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface OrderService {
    // 기본 CRUD
    Order createOrder(List<Long> bookIds);
    Order findOrderById(Long id);
    Page<Order> findAllOrders(Pageable pageable);
    Order updateOrder(Long id, List<Long> bookIds);

    // 상태 관리
    Order updateOrderStatus(Long id, OrderStatus status);
    Order cancelOrder(Long id);
    Page<Order> findOrdersByStatus(OrderStatus status, Pageable pageable);

    // 조회
    Page<Order> findOrdersByPeriod(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    // 통계
    long getTotalOrdersCount();
    long getOrdersCountByStatus(OrderStatus status);
    BigDecimal getTotalRevenue();
    BigDecimal getAverageOrderAmount();
    List<OrderController.DailyOrderStatistics> getDailyOrderStatistics(LocalDateTime startDate, LocalDateTime endDate);
    List<Map<String, Object>> getTopSellingBooks(int limit);
}