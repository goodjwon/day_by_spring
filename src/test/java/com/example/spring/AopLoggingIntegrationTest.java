package com.example.spring;

import com.example.spring.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;

@SpringBootTest(classes = SpringBookstoreApplication.class)
@ActiveProfiles("test")
public class AopLoggingIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Test
    public void testAopLogging_ServiceCall() {
        System.out.println("\n=== AOP 로깅 테스트 시작 ===");
        
        try {
            orderService.createOrder(Arrays.asList(1L, 2L));
        } catch (Exception e) {
            System.out.println("예상된 오류 (테스트 데이터 없음): " + e.getMessage());
        }
        
        System.out.println("=== AOP 로깅 테스트 완료 ===\n");
    }
}