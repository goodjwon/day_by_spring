package com.example.spring.repository.impl;

import com.example.spring.entity.Order;
import com.example.spring.entity.OrderStatus;
import com.example.spring.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface JpaOrderRepository extends JpaRepository<Order, Long>, OrderRepository {

    // 상태별 조회
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    long countByStatus(OrderStatus status);

    // 기간별 조회
    Page<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    // 통계 쿼리
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status != 'CANCELLED'")
    BigDecimal getTotalRevenue();

    @Query("SELECT COALESCE(AVG(o.totalAmount), 0) FROM Order o WHERE o.status != 'CANCELLED'")
    BigDecimal getAverageOrderAmount();

    // 일별 주문 통계
    @Query("SELECT DATE(o.orderDate) as date, COUNT(o) as orderCount, SUM(o.totalAmount) as totalAmount " +
           "FROM Order o " +
           "WHERE o.orderDate BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(o.orderDate) " +
           "ORDER BY DATE(o.orderDate)")
    List<Map<String, Object>> getDailyStatistics(@Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate);

    // 상위 판매 도서
    @Query("SELECT b.id as bookId, b.title as bookTitle, b.author as bookAuthor, " +
           "SUM(oi.quantity) as totalQuantity, SUM(oi.price * oi.quantity) as totalRevenue " +
           "FROM OrderItem oi " +
           "JOIN oi.book b " +
           "JOIN oi.order o " +
           "WHERE o.status != 'CANCELLED' " +
           "GROUP BY b.id, b.title, b.author " +
           "ORDER BY totalQuantity DESC")
    List<Map<String, Object>> getTopSellingBooks(Pageable pageable);
}