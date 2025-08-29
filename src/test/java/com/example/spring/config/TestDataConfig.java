package com.example.spring.config;

import com.example.spring.entity.*;
import com.example.spring.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 테스트 데이터 설정 클래스
 * - 테스트 간 데이터 격리와 재사용성을 보장
 * - 각 테스트가 필요한 데이터만 생성하도록 지원
 */
@TestConfiguration
@RequiredArgsConstructor
public class TestDataConfig {

    /**
     * 테스트 데이터 빌더 - 체인 방식으로 필요한 데이터만 생성
     */
    @Bean
    @Profile("test")
    public TestDataBuilder testDataBuilder() {
        return new TestDataBuilder();
    }

    public static class TestDataBuilder {
        
        /**
         * 기본 회원 데이터 생성
         */
        public Member createDefaultMember() {
            return Member.builder()
                    .name("테스트사용자")
                    .email("test.user@example.com")
                    .membershipType(MembershipType.REGULAR)
                    .joinDate(LocalDateTime.now())
                    .build();
        }

        /**
         * 커스텀 회원 데이터 생성
         */
        public Member createMember(String name, String email, MembershipType type) {
            return Member.builder()
                    .name(name)
                    .email(email)
                    .membershipType(type)
                    .joinDate(LocalDateTime.now())
                    .build();
        }

        /**
         * PREMIUM 회원 생성
         */
        public Member createPremiumMember(String name, String email) {
            return createMember(name, email, MembershipType.PREMIUM);
        }

        /**
         * SUSPENDED 회원 생성
         */
        public Member createSuspendedMember(String name, String email) {
            return createMember(name, email, MembershipType.SUSPENDED);
        }

        /**
         * 기본 도서 데이터 생성
         */
        public Book createDefaultBook() {
            return Book.builder()
                    .title("테스트 도서")
                    .author("테스트 작가")
                    .isbn("978-0000000000")
                    .price(new BigDecimal("30000"))
                    .available(true)
                    .createdDate(LocalDateTime.now())
                    .build();
        }

        /**
         * 커스텀 도서 데이터 생성
         */
        public Book createBook(String title, String author, String isbn, 
                              BigDecimal price, boolean available) {
            return Book.builder()
                    .title(title)
                    .author(author)
                    .isbn(isbn)
                    .price(price)
                    .available(available)
                    .createdDate(LocalDateTime.now())
                    .build();
        }

        /**
         * 대여 불가능한 도서 생성
         */
        public Book createUnavailableBook(String title, String author) {
            return createBook(title, author, "978-0000000001", 
                            new BigDecimal("25000"), false);
        }

        /**
         * 진행 중인 대여 데이터 생성
         */
        public Loan createActiveLoan(Member member, Book book) {
            return Loan.builder()
                    .member(member)
                    .book(book)
                    .loanDate(LocalDateTime.now().minusDays(5))
                    .dueDate(LocalDateTime.now().plusDays(9))
                    .build();
        }

        /**
         * 연체된 대여 데이터 생성
         */
        public Loan createOverdueLoan(Member member, Book book) {
            return Loan.builder()
                    .member(member)
                    .book(book)
                    .loanDate(LocalDateTime.now().minusDays(20))
                    .dueDate(LocalDateTime.now().minusDays(6)) // 6일 전이 반납일
                    .build();
        }

        /**
         * 반납 완료된 대여 데이터 생성
         */
        public Loan createReturnedLoan(Member member, Book book) {
            return Loan.builder()
                    .member(member)
                    .book(book)
                    .loanDate(LocalDateTime.now().minusDays(30))
                    .dueDate(LocalDateTime.now().minusDays(16))
                    .returnDate(LocalDateTime.now().minusDays(15))
                    .build();
        }

        /**
         * 기본 주문 데이터 생성
         */
        public Order createDefaultOrder() {
            return Order.builder()
                    .totalAmount(new BigDecimal("90000"))
                    .orderDate(LocalDateTime.now())
                    .status(OrderStatus.PENDING)
                    .build();
        }

        /**
         * 주문 아이템 생성
         */
        public OrderItem createOrderItem(Order order, Book book, int quantity) {
            return OrderItem.builder()
                    .order(order)
                    .book(book)
                    .quantity(quantity)
                    .price(book.getPrice().multiply(new BigDecimal(quantity)))
                    .build();
        }
    }
}