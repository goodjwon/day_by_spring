package com.example.spring.config;

import com.example.spring.entity.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 테스트 시나리오별 데이터 컨테이너
 * - 각 테스트 유형별로 필요한 데이터를 그룹화
 * - 테스트 코드에서 쉽게 접근할 수 있도록 구조화
 */
public class TestScenario {

    /**
     * 단일 회원 시나리오
     * - 회원 기본 CRUD 테스트
     * - 회원 검증 로직 테스트
     */
    @Getter
    @AllArgsConstructor
    public static class SingleMemberScenario {
        private final Member member;
        
        public Long getMemberId() {
            return member.getId();
        }
        
        public String getMemberEmail() {
            return member.getEmail();
        }
    }

    /**
     * 대여 시나리오
     * - 대여 가능성 검증 테스트
     * - 대여 제한 로직 테스트
     * - 연체 처리 테스트
     */
    @Getter
    @AllArgsConstructor
    public static class LoanScenario {
        private final Member regularMember;      // 일반 회원 (5권 제한)
        private final Member premiumMember;      // 프리미엄 회원 (10권 제한)
        private final Member suspendedMember;    // 정지 회원 (대여 불가)
        private final Book availableBook;        // 대여 가능한 도서
        private final Book unavailableBook;      // 대여 불가능한 도서
        private final List<Loan> existingLoans;  // 기존 대여 목록 (제한 테스트용)
        
        public int getRegularMemberLoanCount() {
            return existingLoans.size(); // 현재 4권 대여 중
        }
        
        public boolean isRegularMemberNearLimit() {
            return existingLoans.size() >= 4; // 5권 제한 중 4권 사용
        }
    }

    /**
     * 주문 시나리오
     * - 주문 생성 테스트
     * - 주문 아이템 관리 테스트
     * - 결제 처리 테스트
     */
    @Getter
    @AllArgsConstructor
    public static class OrderScenario {
        private final List<Book> availableBooks;
        
        public Book getFirstBook() {
            return availableBooks.get(0);
        }
        
        public Book getSecondBook() {
            return availableBooks.get(1);
        }
        
        public List<Long> getAllBookIds() {
            return availableBooks.stream()
                    .map(Book::getId)
                    .toList();
        }
        
        public java.math.BigDecimal getTotalPrice() {
            return availableBooks.stream()
                    .map(Book::getPrice)
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        }
    }

    /**
     * 복합 시나리오
     * - 통합 테스트용
     * - 서비스 간 상호작용 테스트
     * - 통계 및 리포트 테스트
     */
    @Getter
    @AllArgsConstructor
    public static class ComplexScenario {
        private final List<Member> members;
        private final List<Book> books;
        private final List<Loan> loans;
        private final List<Order> orders;
        
        public Member getRegularMember() {
            return members.stream()
                    .filter(m -> m.getMembershipType() == MembershipType.REGULAR)
                    .findFirst()
                    .orElse(null);
        }
        
        public Member getPremiumMember() {
            return members.stream()
                    .filter(m -> m.getMembershipType() == MembershipType.PREMIUM)
                    .findFirst()
                    .orElse(null);
        }
        
        public long getActiveLoanCount() {
            return loans.stream()
                    .filter(loan -> loan.getReturnDate() == null)
                    .count();
        }
        
        public long getReturnedLoanCount() {
            return loans.stream()
                    .filter(loan -> loan.getReturnDate() != null)
                    .count();
        }
    }
}