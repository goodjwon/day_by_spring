package com.example.spring.service.impl;

import com.example.spring.entity.Order;
import com.example.spring.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

// 1. 이메일 서비스 구현체들
@Service
@ConditionalOnProperty(name = "bookstore.email.smtp.enabled", havingValue = "true")
@Slf4j
public class SmtpEmailService implements EmailService {

    @Override
    public void sendOrderConfirmation(Order order) {
        log.info("SMTP를 통한 주문 확인 이메일 발송 시작");

        // 실제 SMTP 발송 로직 (여기서는 시뮬레이션)
        System.out.println("=== SMTP 주문 확인 이메일 ===");
        System.out.println("주문 ID: " + order.getId());
        System.out.println("총 금액: " + order.getTotalAmount());
        System.out.println("주문 일시: " + order.getOrderDate());
        System.out.println("도서 목록:");
        order.getBooks().forEach(book ->
                System.out.println("  - " + book.getTitle() + " (" + book.getPrice() + "원)"));
        System.out.println("SMTP 이메일 발송 완료");

        log.info("SMTP 주문 확인 이메일 발송 완료 - 주문 ID: {}", order.getId());
    }

    @Override
    public void sendOrderShipped(Order order) {
        log.info("주문 배송 시작 이메일 발송 - 주문 ID: {}", order.getId());
        System.out.println("=== 배송 시작 알림 이메일 ===");
        System.out.println("주문 ID: " + order.getId() + " 배송이 시작되었습니다.");
    }
}