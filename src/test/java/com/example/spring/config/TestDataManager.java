package com.example.spring.config;

import com.example.spring.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

/**
 * 테스트 데이터 매니저
 * - 테스트 실행 시 필요한 데이터를 동적으로 생성/관리
 * - @Transactional과 함께 사용하여 테스트 간 데이터 격리 보장
 */
@Component
@RequiredArgsConstructor
@ActiveProfiles("test")
public class TestDataManager {

    public final TestDataConfig.TestDataBuilder testDataBuilder;
    
    /**
     * 단일 회원 시나리오 - 회원 기본 기능 테스트용
     */
    public TestScenario.SingleMemberScenario createSingleMemberScenario(TestEntityManager entityManager) {
        Member member = testDataBuilder.createDefaultMember();
        entityManager.persistAndFlush(member);
        
        return new TestScenario.SingleMemberScenario(member);
    }

    /**
     * 대여 시나리오 - 대여 관련 기능 테스트용
     */
    public TestScenario.LoanScenario createLoanScenario(TestEntityManager entityManager) {
        // 1. 회원 생성
        Member regularMember = testDataBuilder.createDefaultMember();
        Member premiumMember = testDataBuilder.createPremiumMember("프리미엄사용자", "premium@test.com");
        Member suspendedMember = testDataBuilder.createSuspendedMember("정지사용자", "suspended@test.com");
        
        entityManager.persist(regularMember);
        entityManager.persist(premiumMember);
        entityManager.persist(suspendedMember);
        
        // 2. 도서 생성
        Book availableBook = testDataBuilder.createDefaultBook();
        Book unavailableBook = testDataBuilder.createUnavailableBook("대여불가도서", "테스트작가");
        
        entityManager.persist(availableBook);
        entityManager.persist(unavailableBook);
        
        // 3. 기존 대여 생성 (제한 테스트용)
        List<Loan> existingLoans = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Book book = testDataBuilder.createBook("대여중도서" + i, "작가" + i, 
                                                 "978-000000000" + i, null, false);
            entityManager.persist(book);
            
            Loan loan = testDataBuilder.createActiveLoan(regularMember, book);
            entityManager.persist(loan);
            existingLoans.add(loan);
        }
        
        entityManager.flush();
        
        return new TestScenario.LoanScenario(
            regularMember, premiumMember, suspendedMember,
            availableBook, unavailableBook, existingLoans
        );
    }

    /**
     * 주문 시나리오 - 주문 관련 기능 테스트용
     */
    public TestScenario.OrderScenario createOrderScenario(TestEntityManager entityManager) {
        // 1. 도서들 생성
        List<Book> books = List.of(
            testDataBuilder.createBook("Clean Code", "Robert Martin", "978-0132350884", 
                                     java.math.BigDecimal.valueOf(45000), true),
            testDataBuilder.createBook("Spring in Action", "Craig Walls", "978-1617294945", 
                                     java.math.BigDecimal.valueOf(52000), true),
            testDataBuilder.createBook("Effective Java", "Joshua Bloch", "978-0134685991", 
                                     java.math.BigDecimal.valueOf(48000), true)
        );
        
        books.forEach(entityManager::persist);
        entityManager.flush();
        
        return new TestScenario.OrderScenario(books);
    }

    /**
     * 복합 시나리오 - 통합 테스트용 (회원 + 도서 + 대여 + 주문)
     */
    public TestScenario.ComplexScenario createComplexScenario(TestEntityManager entityManager) {
        // 회원들
        Member regularMember = testDataBuilder.createMember("김정규", "kim.regular@test.com", MembershipType.REGULAR);
        Member premiumMember = testDataBuilder.createMember("박프리미엄", "park.premium@test.com", MembershipType.PREMIUM);
        
        entityManager.persist(regularMember);
        entityManager.persist(premiumMember);
        
        // 도서들
        List<Book> books = List.of(
            testDataBuilder.createBook("Spring Boot Guide", "김스프링", "978-1111111111", 
                                     java.math.BigDecimal.valueOf(35000), true),
            testDataBuilder.createBook("JPA Programming", "박제이피에이", "978-2222222222", 
                                     java.math.BigDecimal.valueOf(40000), true)
        );
        books.forEach(entityManager::persist);
        
        // 기존 대여 (통계용)
        Loan activeLoan = testDataBuilder.createActiveLoan(regularMember, books.get(0));
        Loan returnedLoan = testDataBuilder.createReturnedLoan(premiumMember, books.get(1));
        
        entityManager.persist(activeLoan);
        entityManager.persist(returnedLoan);
        
        // 주문 (통계용)
        Order order = testDataBuilder.createDefaultOrder();
        entityManager.persist(order);
        
        OrderItem orderItem = testDataBuilder.createOrderItem(order, books.get(0), 2);
        entityManager.persist(orderItem);
        
        entityManager.flush();
        
        return new TestScenario.ComplexScenario(
            List.of(regularMember, premiumMember),
            books,
            List.of(activeLoan, returnedLoan),
            List.of(order)
        );
    }

    /**
     * 데이터 정리 - 테스트 후 명시적 정리가 필요한 경우
     */
    public void cleanupTestData(TestEntityManager entityManager, Object... entities) {
        for (Object entity : entities) {
            if (entityManager.find(entity.getClass(), getEntityId(entity)) != null) {
                entityManager.remove(entity);
            }
        }
        entityManager.flush();
    }

    private Object getEntityId(Object entity) {
        // 간단한 ID 추출 로직 (실제로는 reflection 사용)
        if (entity instanceof Member) return ((Member) entity).getId();
        if (entity instanceof Book) return ((Book) entity).getId();
        if (entity instanceof Loan) return ((Loan) entity).getId();
        if (entity instanceof Order) return ((Order) entity).getId();
        return null;
    }
}