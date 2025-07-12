package com.example.spring.traditional;
import com.example.spring.entity.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TraditionalOrderServiceTest {

    private TraditionalOrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new TraditionalOrderService();
    }

    @AfterEach
    void tearDown() {
        if (orderService != null) {
            orderService.cleanup();
        }
    }

    @Test
    void createOrder_정상주문() {
        // Given - DB에 존재하는 도서 ID 사용 (1L은 일반적으로 존재)
        List<Long> bookIds = Arrays.asList(1L);

        // When & Then - 예외가 발생하지 않으면 성공
        assertDoesNotThrow(() -> {
            Order order = orderService.createOrder(bookIds);

            // 기본적인 검증
            assertNotNull(order);
            assertNotNull(order.getId());
            assertNotNull(order.getOrderDate());
            assertNotNull(order.getTotalAmount());
            assertTrue(order.getTotalAmount().compareTo(BigDecimal.ZERO) > 0);
        });
    }

    @Test
    void createOrder_여러도서주문() {
        // Given - 여러 도서 ID (실제 존재하는 ID 사용)
        List<Long> bookIds = Arrays.asList(1L, 2L);

        // When & Then
        assertDoesNotThrow(() -> {
            Order order = orderService.createOrder(bookIds);

            assertNotNull(order);
            assertNotNull(order.getTotalAmount());
            // 여러 도서이므로 더 큰 금액일 것으로 예상
            assertTrue(order.getTotalAmount().compareTo(BigDecimal.ZERO) > 0);
        });
    }

    @Test
    void createOrder_존재하지않는도서() {
        // Given - 존재하지 않는 도서 ID
        List<Long> bookIds = Arrays.asList(999L);

        // When & Then - RuntimeException이 발생해야 함
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(bookIds);
        });

        assertTrue(exception.getMessage().contains("존재하지 않는 도서"));
    }

    @Test
    void createOrder_빈주문목록() {
        // Given - 빈 주문 목록
        List<Long> bookIds = Collections.emptyList();

        // When & Then - 빈 목록으로도 주문 생성 가능해야 함
        assertDoesNotThrow(() -> {
            Order order = orderService.createOrder(bookIds);

            assertNotNull(order);
            assertEquals(BigDecimal.ZERO, order.getTotalAmount());
        });
    }

    @Test
    void createOrder_null입력() {
        // Given - null 입력
        List<Long> bookIds = null;

        // When & Then - NullPointerException 발생 예상
        assertThrows(NullPointerException.class, () -> {
            orderService.createOrder(bookIds);
        });
    }

    @Test
    void createOrder_혼합된주문목록() {
        // Given - 존재하는 도서와 존재하지 않는 도서 혼합
        List<Long> bookIds = Arrays.asList(1L, 999L);

        // When & Then - 존재하지 않는 도서로 인해 실패해야 함
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(bookIds);
        });

        assertTrue(exception.getMessage().contains("존재하지 않는 도서"));
    }

    @Test
    void createOrder_중복도서주문() {
        // Given - 같은 도서 ID 중복
        List<Long> bookIds = Arrays.asList(1L, 1L);

        // When & Then - 중복 주문도 정상 처리되어야 함
        assertDoesNotThrow(() -> {
            Order order = orderService.createOrder(bookIds);

            assertNotNull(order);
            // 같은 도서 2권이므로 가격이 2배가 되어야 함
            assertTrue(order.getTotalAmount().compareTo(BigDecimal.ZERO) > 0);
        });
    }
}

/**
 * 문제:
 * 실제 DB에 연결해야 테스트 가능
 * 이메일이 실제로 발송됨
 * 테스트 속도가 느림
 * 외부 시스템 장애 시 테스트 실패
 */