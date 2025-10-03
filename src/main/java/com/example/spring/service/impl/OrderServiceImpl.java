package com.example.spring.service.impl;

import com.example.spring.controller.OrderController;
import com.example.spring.entity.Book;
import com.example.spring.entity.Order;
import com.example.spring.entity.OrderItem;
import com.example.spring.entity.OrderStatus;
import com.example.spring.repository.BookRepository;
import com.example.spring.repository.impl.JpaOrderRepository;
import com.example.spring.service.EmailService;
import com.example.spring.service.LoggingService;
import com.example.spring.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final BookRepository bookRepository;
    private final JpaOrderRepository orderRepository;
    private final EmailService emailService;
    private final LoggingService loggingService;

    @Override
    @Transactional
    public Order createOrder(List<Long> bookIds) {
        loggingService.log("주문 생성 시작 - 도서 ID: " + bookIds);

        long startTime = System.currentTimeMillis();

        try {
            // 1. 도서 조회 및 검증
            List<Book> books = new ArrayList<>();
            BigDecimal total = BigDecimal.ZERO;

            for (Long bookId : bookIds) {
                Book book = bookRepository.findById(bookId)
                        .orElseThrow(() -> {
                            loggingService.error("유효한 도서가 없습니다.", null);
                            return new IllegalArgumentException("유효한 도서가 없습니다.");
                        });
                books.add(book);
                total = total.add(book.getPrice());
            }

            // 2. 주문 생성
            Order order = Order.builder()
                    .totalAmount(total)
                    .orderDate(LocalDateTime.now())
                    .status(OrderStatus.PENDING)
                    .build();

            // 3. 주문 아이템 생성 및 추가
            for (Book book : books) {
                OrderItem orderItem = OrderItem.builder()
                        .book(book)
                        .quantity(1)
                        .price(book.getPrice())
                        .build();
                order.addOrderItem(orderItem);
            }

            // 4. 주문 저장
            Order savedOrder = orderRepository.save(order);

            // 5. 이메일 발송
            emailService.sendOrderConfirmation(savedOrder);

            long endTime = System.currentTimeMillis();
            loggingService.log("주문 생성 완료 - 실행 시간: " + (endTime - startTime) + "ms");

            return savedOrder;

        } catch (Exception e) {
            loggingService.error("주문 생성 실패", e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Order findOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> findAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public Order updateOrder(Long id, List<Long> bookIds) {
        loggingService.log("주문 수정 시작 - ID: " + id);

        Order order = findOrderById(id);

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("PENDING 상태의 주문만 수정할 수 있습니다.");
        }

        // 기존 아이템 제거
        order.getOrderItems().clear();

        // 새로운 도서로 아이템 생성
        List<Book> books = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (Long bookId : bookIds) {
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new IllegalArgumentException("유효한 도서가 없습니다."));
            books.add(book);
            total = total.add(book.getPrice());
        }

        for (Book book : books) {
            OrderItem orderItem = OrderItem.builder()
                    .book(book)
                    .quantity(1)
                    .price(book.getPrice())
                    .build();
            order.addOrderItem(orderItem);
        }

        order.setTotalAmount(total);

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order updateOrderStatus(Long id, OrderStatus status) {
        loggingService.log("주문 상태 변경 - ID: " + id + ", 상태: " + status);

        Order order = findOrderById(id);
        order.setStatus(status);

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order cancelOrder(Long id) {
        loggingService.log("주문 취소 - ID: " + id);

        Order order = findOrderById(id);

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("배송 완료된 주문은 취소할 수 없습니다.");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("이미 취소된 주문입니다.");
        }

        order.setStatus(OrderStatus.CANCELLED);

        return orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> findOrdersByStatus(OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Order> findOrdersByPeriod(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return orderRepository.findByOrderDateBetween(startDate, endDate, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalOrdersCount() {
        return orderRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long getOrdersCountByStatus(OrderStatus status) {
        return orderRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalRevenue() {
        return orderRepository.getTotalRevenue();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getAverageOrderAmount() {
        return orderRepository.getAverageOrderAmount();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderController.DailyOrderStatistics> getDailyOrderStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        List<Map<String, Object>> results = orderRepository.getDailyStatistics(startDate, endDate);

        return results.stream()
                .map(result -> OrderController.DailyOrderStatistics.builder()
                        .date((LocalDateTime) result.get("date"))
                        .orderCount((Long) result.get("orderCount"))
                        .totalAmount((BigDecimal) result.get("totalAmount"))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTopSellingBooks(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return orderRepository.getTopSellingBooks(pageable);
    }
}
