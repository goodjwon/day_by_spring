package com.example.spring;

import com.example.spring.entity.Book;
import com.example.spring.entity.Order;
import com.example.spring.entity.OrderItem;
import com.example.spring.repository.BookRepository;
import com.example.spring.repository.OrderRepository;
import com.example.spring.service.EmailService;
import com.example.spring.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = SpringBookstoreApplication.class) // 메인 클래스 명시적 지정
@ActiveProfiles("test") // test 프로파일 사용 (H2 DB)
@Transactional // 각 테스트 후 롤백
class OrderServiceIntegrationTest {

    @Autowired private OrderService orderService;
    @Autowired private BookRepository bookRepository;
    @Autowired private com.example.spring.repository.impl.JpaOrderRepository orderRepository;

    @MockitoBean private EmailService emailService; // 새로운 @MockitoBean 사용

    @PersistenceContext private EntityManager entityManager;

    private Book testBook1;
    private Book testBook2;
    private Book testBook3;

    @BeforeEach
    void setUp() {
        // 기존 주문 데이터 직접 삭제 (EntityManager 사용)
        entityManager.createQuery("DELETE FROM OrderItem").executeUpdate();
        entityManager.createQuery("DELETE FROM Order").executeUpdate();
        entityManager.flush();
        entityManager.clear();

        // data.sql에서 삽입된 기존 도서 데이터 활용
        testBook1 = bookRepository.findById(1L).orElseThrow(); // Clean Code
        testBook2 = bookRepository.findById(2L).orElseThrow(); // Spring in Action
        testBook3 = bookRepository.findById(3L).orElseThrow(); // Effective Java
    }

    @Test
    void createOrder_정상주문_실제DB검증() {
        // Given
        List<Long> bookIds = Arrays.asList(testBook1.getId());

        // When - 실제 서비스 + 실제 DB
        Order result = orderService.createOrder(bookIds);

        // Then - 실제 DB에서 검증
        // 1. 반환된 Order 검증
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getTotalAmount()).isEqualTo(new BigDecimal("45000")); // Clean Code 가격
        assertThat(result.getOrderDate()).isNotNull();
        assertThat(result.getOrderItems()).hasSize(1);

        // 2. DB에서 직접 조회해서 검증
        Optional<Order> savedOrder = orderRepository.findById(result.getId());
        assertThat(savedOrder).isPresent();

        Order dbOrder = savedOrder.get();
        assertThat(dbOrder.getTotalAmount()).isEqualTo(new BigDecimal("45000"));
        assertThat(dbOrder.getOrderItems()).hasSize(1);

        OrderItem orderItem = dbOrder.getOrderItems().get(0);
        assertThat(orderItem.getBook().getId()).isEqualTo(testBook1.getId());
        assertThat(orderItem.getPrice()).isEqualTo(new BigDecimal("45000"));
        assertThat(orderItem.getQuantity()).isEqualTo(1);

        // 3. 이메일 발송 확인
        verify(emailService).sendOrderConfirmation(any(Order.class));
    }

    @Test
    void createOrder_여러도서주문_실제DB검증() {
        // Given
        List<Long> bookIds = Arrays.asList(testBook1.getId(), testBook2.getId(), testBook3.getId());

        // When
        Order result = orderService.createOrder(bookIds);

        // Then
        // 1. 총액 검증 (45000 + 52000 + 48000 = 145000)
        assertThat(result.getTotalAmount()).isEqualByComparingTo(new BigDecimal("145000"));
        assertThat(result.getOrderItems()).hasSize(3);

        // 2. DB에서 실제 저장 확인
        entityManager.flush(); // DB 반영 강제
        entityManager.clear(); // 캐시 클리어

        Order dbOrder = orderRepository.findById(result.getId()).orElseThrow();
        assertThat(dbOrder.getOrderItems()).hasSize(3);

        // 3. 각 OrderItem 검증
        List<OrderItem> orderItems = dbOrder.getOrderItems();
        BigDecimal totalFromItems = orderItems.stream()
                .map(OrderItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertThat(totalFromItems).isEqualByComparingTo(new BigDecimal("145000"));
    }

    @Test
    void createOrder_존재하지않는도서_실제DB검증() {
        // Given - 존재하지 않는 ID
        List<Long> bookIds = Arrays.asList(999999L);

        // When & Then
        assertThatThrownBy(() -> orderService.createOrder(bookIds))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효한 도서가 없습니다.");

        // 실제 DB에 Order가 저장되지 않았는지 확인
        List<Order> allOrders = orderRepository.findAll();
        assertThat(allOrders).isEmpty();

        // 이메일이 발송되지 않았는지 확인
        verify(emailService, never()).sendOrderConfirmation(any(Order.class));
    }

    @Test
    void createOrder_빈주문목록_실제DB검증() {
        // Given
        List<Long> bookIds = Collections.emptyList();

        // When
        Order result = orderService.createOrder(bookIds);

        // Then
        assertThat(result.getTotalAmount()).isEqualTo(BigDecimal.ZERO);
        assertThat(result.getOrderItems()).isEmpty();

        // DB에서 확인
        Order dbOrder = orderRepository.findById(result.getId()).orElseThrow();
        assertThat(dbOrder.getTotalAmount()).isEqualTo(BigDecimal.ZERO);
        assertThat(dbOrder.getOrderItems()).isEmpty();
    }

    @Test
    void createOrder_중복도서주문_실제DB검증() {
        // Given - 같은 책 2번 주문
        List<Long> bookIds = Arrays.asList(testBook1.getId(), testBook1.getId());

        // When
        Order result = orderService.createOrder(bookIds);

        // Then
        // 1. 총액이 책 가격 * 2인지 확인
        assertThat(result.getTotalAmount()).isEqualTo(new BigDecimal("70000")); // 35000 * 2
        assertThat(result.getOrderItems()).hasSize(2);

        // 2. DB에서 확인
        Order dbOrder = orderRepository.findById(result.getId()).orElseThrow();
        assertThat(dbOrder.getOrderItems()).hasSize(2);

        // 각 OrderItem이 같은 책을 참조하는지 확인
        List<OrderItem> orderItems = dbOrder.getOrderItems();
        assertThat(orderItems.get(0).getBook().getId()).isEqualTo(testBook1.getId());
        assertThat(orderItems.get(1).getBook().getId()).isEqualTo(testBook1.getId());
    }

    @Test
    void createOrder_혼합된주문목록_실제DB검증() {
        // Given - 존재하는 책 + 존재하지 않는 책
        List<Long> bookIds = Arrays.asList(testBook1.getId(), 999999L);

        // When & Then
        assertThatThrownBy(() -> orderService.createOrder(bookIds))
                .isInstanceOf(IllegalArgumentException.class);

        // 트랜잭션 롤백으로 아무것도 저장되지 않았는지 확인
        List<Order> allOrders = orderRepository.findAll();
        assertThat(allOrders).isEmpty();
    }

    @Test
    @Transactional
    void createOrder_트랜잭션롤백테스트() {
        // Given
        List<Long> bookIds = Arrays.asList(testBook1.getId());

        // 이메일 서비스에서 예외 발생하도록 설정
        doThrow(new RuntimeException("이메일 발송 실패"))
                .when(emailService).sendOrderConfirmation(any(Order.class));

        // When & Then
        assertThatThrownBy(() -> orderService.createOrder(bookIds))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("이메일 발송 실패");

        // 트랜잭션 롤백으로 Order가 저장되지 않았는지 확인
        entityManager.flush();
        List<Order> allOrders = orderRepository.findAll();
        assertThat(allOrders).isEmpty();
    }

    @Test
    void findOrderById_실제DB조회() {
        // Given - 실제 주문 생성
        Order savedOrder = orderService.createOrder(Arrays.asList(testBook1.getId()));
        entityManager.flush();
        entityManager.clear();

        // When
        Order foundOrder = orderService.findOrderById(savedOrder.getId());

        // Then
        assertThat(foundOrder).isNotNull();
        assertThat(foundOrder.getId()).isEqualTo(savedOrder.getId());
        assertThat(foundOrder.getTotalAmount()).isEqualTo(new BigDecimal("35000"));
        assertThat(foundOrder.getOrderItems()).hasSize(1);
    }

    @Test
    void findOrderById_존재하지않는주문_실제DB조회() {
        // Given - 존재하지 않는 ID
        Long nonExistentId = 999999L;

        // When & Then
        assertThatThrownBy(() -> orderService.findOrderById(nonExistentId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("주문을 찾을 수 없습니다: " + nonExistentId);
    }

    @Test
    void findAllOrders_실제DB조회() {
        // Given - 여러 주문 생성
        Order order1 = orderService.createOrder(Arrays.asList(testBook1.getId()));         // Clean Code: 45000
        Order order2 = orderService.createOrder(Arrays.asList(testBook2.getId(), testBook3.getId())); // Spring in Action + Effective Java: 52000 + 48000 = 100000
        entityManager.flush();

        // When
        org.springframework.data.domain.Page<Order> page = orderService.findAllOrders(org.springframework.data.domain.PageRequest.of(0, 10));
        List<Order> allOrders = page.getContent();

        // Then
        assertThat(allOrders).hasSize(2);
        assertThat(allOrders).extracting(Order::getId)
                .contains(order1.getId(), order2.getId());

        // 총액 확인 - data.sql의 실제 가격 사용 (isEqualByComparingTo 사용)
        assertThat(allOrders).extracting(Order::getTotalAmount)
                .usingComparatorForType(BigDecimal::compareTo, BigDecimal.class) // BigDecimal 비교 방식 지정
                .contains(new BigDecimal("45000"), new BigDecimal("100000")); // Clean Code, Spring+Effective
    }

    @Test
    void createOrder_DB제약조건검증() {
        // Given
        List<Long> bookIds = Arrays.asList(testBook1.getId());

        // When
        Order result = orderService.createOrder(bookIds);

        // Then - DB 제약조건 확인
        entityManager.flush();

        // 1. Order 필수 필드 확인
        assertThat(result.getId()).isNotNull();
        assertThat(result.getTotalAmount()).isNotNull();
        assertThat(result.getOrderDate()).isNotNull();

        // 2. OrderItem과 Book의 관계 확인
        OrderItem orderItem = result.getOrderItems().get(0);
        assertThat(orderItem.getBook()).isNotNull();
        assertThat(orderItem.getBook().getId()).isEqualTo(testBook1.getId());

        // 3. 실제 DB에서 FK 관계 확인
        Order dbOrder = orderRepository.findById(result.getId()).orElseThrow();
        Book relatedBook = dbOrder.getOrderItems().get(0).getBook();
        assertThat(relatedBook.getTitle()).isEqualTo("스프링 부트 완벽 가이드");
    }
}