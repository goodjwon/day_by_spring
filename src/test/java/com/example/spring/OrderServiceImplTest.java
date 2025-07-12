package com.example.spring;

import com.example.spring.entity.Book;
import com.example.spring.entity.Order;
import com.example.spring.repository.BookRepository;
import com.example.spring.repository.OrderRepository;
import com.example.spring.service.EmailService;
import com.example.spring.service.LoggingService;
import com.example.spring.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock private BookRepository bookRepository;    // OrderService가 작동하기 위해 필수
    @Mock private OrderRepository orderRepository;  // OrderService가 작동하기 위해 필수
    @Mock private EmailService emailService;        // OrderService가 작동하기 위해 필수
    @Mock private LoggingService loggingService;    // OrderService가 작동하기 위해 필수

    @InjectMocks private OrderServiceImpl orderService;

    @Test
    void createOrder_정상주문() {
        // Given
        Book book = createTestBook(1L, "테스트책", new BigDecimal("10000"));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L); // ID 설정
            return order;
        });

        // When
        Order result = orderService.createOrder(List.of(1L));

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTotalAmount()).isEqualTo(new BigDecimal("10000"));
        assertThat(result.getOrderDate()).isNotNull();

        verify(bookRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
        verify(emailService).sendOrderConfirmation(any(Order.class));
        verify(loggingService).log(contains("주문 생성 시작"));
        verify(loggingService).log(contains("주문 생성 완료"));
    }

    @Test
    void createOrder_여러도서주문() {
        // Given
        Book book1 = createTestBook(1L, "테스트책1", new BigDecimal("10000"));
        Book book2 = createTestBook(2L, "테스트책2", new BigDecimal("15000"));

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));
        when(bookRepository.findById(2L)).thenReturn(Optional.of(book2));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });

        // When
        Order result = orderService.createOrder(Arrays.asList(1L, 2L));

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalAmount()).isEqualTo(new BigDecimal("25000")); // 10000 + 15000

        verify(bookRepository).findById(1L);
        verify(bookRepository).findById(2L);
        verify(orderRepository).save(any(Order.class));
        verify(emailService).sendOrderConfirmation(any(Order.class));
    }

    @Test
    void createOrder_존재하지않는도서() {
        // Given
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> orderService.createOrder(List.of(999L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효한 도서가 없습니다.");

        verify(bookRepository).findById(999L);
        verify(orderRepository, never()).save(any(Order.class));
        verify(emailService, never()).sendOrderConfirmation(any(Order.class));
        verify(loggingService).error(eq("주문 생성 실패"), any(Exception.class));
    }

    @Test
    void createOrder_빈주문목록() {
        // Given
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });

        // When
        Order result = orderService.createOrder(Collections.emptyList());

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalAmount()).isEqualTo(BigDecimal.ZERO);

        verify(bookRepository, never()).findById(anyLong());
        verify(orderRepository).save(any(Order.class));
        verify(emailService).sendOrderConfirmation(any(Order.class));
    }

    @Test
    void createOrder_null입력() {
        // Given - null 입력

        // When & Then
        assertThatThrownBy(() -> orderService.createOrder(null))
                .isInstanceOf(NullPointerException.class);

        verify(bookRepository, never()).findById(anyLong());
        verify(orderRepository, never()).save(any(Order.class));
        verify(emailService, never()).sendOrderConfirmation(any(Order.class));
        verify(loggingService).error(eq("주문 생성 실패"), any(Exception.class));
    }

    @Test
    void createOrder_혼합된주문목록() {
        // Given
        Book book = createTestBook(1L, "테스트책", new BigDecimal("10000"));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> orderService.createOrder(Arrays.asList(1L, 999L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효한 도서가 없습니다.");

        verify(bookRepository).findById(1L);
        verify(bookRepository).findById(999L);
        verify(orderRepository, never()).save(any(Order.class));
        verify(emailService, never()).sendOrderConfirmation(any(Order.class));
        verify(loggingService).error(eq("주문 생성 실패"), any(Exception.class));
    }

    @Test
    void createOrder_중복도서주문() {
        // Given
        Book book = createTestBook(1L, "테스트책", new BigDecimal("10000"));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });

        // When
        Order result = orderService.createOrder(Arrays.asList(1L, 1L));

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalAmount()).isEqualTo(new BigDecimal("20000")); // 10000 * 2

        verify(bookRepository, times(2)).findById(1L); // 두 번 호출됨
        verify(orderRepository).save(any(Order.class));
        verify(emailService).sendOrderConfirmation(any(Order.class));
    }

    @Test
    void findOrderById_정상조회() {
        // Given
        Order expectedOrder = createTestOrder(1L, new BigDecimal("10000"));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(expectedOrder));

        // When
        Order result = orderService.findOrderById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTotalAmount()).isEqualTo(new BigDecimal("10000"));

        verify(orderRepository).findById(1L);
    }

    @Test
    void findOrderById_존재하지않는주문() {
        // Given
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> orderService.findOrderById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("주문을 찾을 수 없습니다: 999");

        verify(orderRepository).findById(999L);
    }

    @Test
    void findAllOrders_전체조회() {
        // Given
        List<Order> expectedOrders = Arrays.asList(
                createTestOrder(1L, new BigDecimal("10000")),
                createTestOrder(2L, new BigDecimal("20000"))
        );
        when(orderRepository.findAll()).thenReturn(expectedOrders);

        // When
        List<Order> result = orderService.findAllOrders();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTotalAmount()).isEqualTo(new BigDecimal("10000"));
        assertThat(result.get(1).getTotalAmount()).isEqualTo(new BigDecimal("20000"));

        verify(orderRepository).findAll();
    }

    // 테스트 헬퍼 메서드들
    private Book createTestBook(Long id, String title, BigDecimal price) {
        return Book.builder()
                .id(id)
                .title(title)
                .author("테스트 저자")
                .price(price)
                .build();
    }

    private Order createTestOrder(Long id, BigDecimal totalAmount) {
        return Order.builder()
                .id(id)
                .totalAmount(totalAmount)
                .orderDate(LocalDateTime.now())
                .build();
    }
}