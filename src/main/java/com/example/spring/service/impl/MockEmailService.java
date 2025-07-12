package com.example.spring.service.impl;

import com.example.spring.entity.Order;
import com.example.spring.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

// 2. Mock 이메일 서비스 (테스트/개발용)
@Service
@ConditionalOnProperty(name = "bookstore.email.smtp.enabled", havingValue = "false", matchIfMissing = true)
@Slf4j
public class MockEmailService implements EmailService {

    @Override
    public void sendOrderConfirmation(Order order) {
        log.info("Mock 이메일 서비스 - 주문 확인");
        System.out.println("=== Mock 이메일 서비스 ===");
        System.out.println("주문 확인 이메일 발송 시뮬레이션");
        System.out.println("주문 ID: " + order.getId());
        System.out.println("총 금액: " + order.getTotalAmount());
    }

    @Override
    public void sendOrderShipped(Order order) {
        log.info("Mock 이메일 서비스 - 배송 시작");
        System.out.println("배송 시작 이메일 발송 시뮬레이션");
    }
}