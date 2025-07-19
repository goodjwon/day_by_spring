package com.example.spring.service.impl;

import com.example.spring.entity.Book;
import com.example.spring.entity.Order;
import com.example.spring.entity.OrderItem;
import com.example.spring.repository.BookRepository;
import com.example.spring.repository.OrderRepository;
import com.example.spring.service.EmailService;
import com.example.spring.service.LoggingService;
import com.example.spring.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final BookRepository bookRepository;
    private final OrderRepository orderRepository;
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

            // 4. 이메일 발송
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
    public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }
}
