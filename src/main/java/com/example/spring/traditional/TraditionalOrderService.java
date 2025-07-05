package com.example.spring.traditional;

import com.example.spring.entity.Book;
import com.example.spring.entity.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// 4. 비즈니스 로직 - 모든 문제점이 집약된 클래스
public class TraditionalOrderService {
    private final TraditionalBookRepository bookRepository;
    private final TraditionalEmailService emailService;
    private final TraditionalLoggingService loggingService;

    public TraditionalOrderService() {
        // 강한 결합: 구체 클래스에 직접 의존
        // 테스트 시 Mock 객체 사용 불가
        this.bookRepository = new TraditionalBookRepository();
        this.emailService = new TraditionalEmailService();
        this.loggingService = new TraditionalLoggingService();
    }

    public Order createOrder(List<Long> bookIds) {
        // 로깅 코드가 비즈니스 로직과 섞임
        loggingService.log("주문 생성 시작 - 도서 ID: " + bookIds);

        long startTime = System.currentTimeMillis();

        try {
            // 1. 도서 조회 및 검증
            List<Book> books = new ArrayList<>();
            BigDecimal total = BigDecimal.ZERO;

            for (Long bookId : bookIds) {
                Book book = bookRepository.findById(bookId);
                if (book == null) {
                    loggingService.error("존재하지 않는 도서 ID: " + bookId, null);
                    throw new RuntimeException("존재하지 않는 도서: " + bookId);
                }
                books.add(book);
                total = total.add(book.getPrice());
            }

            // 2. 주문 생성
            Order order = Order.builder()
                    .totalAmount(total)
                    .orderDate(LocalDateTime.now())
                    .build();

            // 임시 ID 설정 (실제로는 DB에 저장 후 생성됨)
            order.setId(System.currentTimeMillis());

            // 3. 이메일 발송
            emailService.sendOrderConfirmation(order);

            long endTime = System.currentTimeMillis();
            loggingService.log("주문 생성 완료 - 실행 시간: " + (endTime - startTime) + "ms");

            return order;

        } catch (Exception e) {
            loggingService.error("주문 생성 실패", e);
            throw e;
        }
    }

    // 자원 정리 메서드 - 개발자가 직접 호출해야 함
    public void cleanup() {
        if (bookRepository != null) {
            bookRepository.close();
        }
    }
}