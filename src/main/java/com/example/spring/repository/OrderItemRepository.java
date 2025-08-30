package com.example.spring.repository;

import com.example.spring.entity.OrderItem;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository {
    Optional<OrderItem> findById(Long id);
    List<OrderItem> findAll();
    OrderItem save(OrderItem orderItem);
    void deleteById(Long id);
    
    List<OrderItem> findByOrderId(Long orderId);
    List<OrderItem> findByBookId(Long bookId);
    List<OrderItem> findByOrderIdAndBookId(Long orderId, Long bookId);

    default OrderItem findOrderItemById(Long id) {
        return findById(id).orElse(null);
    }
}