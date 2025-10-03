package com.example.spring.repository;

import com.example.spring.entity.Book;
import com.example.spring.entity.Order;
import com.example.spring.entity.OrderItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class OrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    private Book createAndSaveBook(String title, String author, BigDecimal price) {
        Book book = Book.builder()
                .title(title)
                .author(author)
                .price(price)
                .isbn("ISBN" + System.currentTimeMillis())
                .available(true)
                .createdDate(LocalDateTime.now())
                .build();
        return entityManager.persistAndFlush(book);
    }

    @Test
    public void save_신규주문_저장성공() {
        // Given
        Order newOrder = Order.builder()
                .totalAmount(new BigDecimal("10000"))
                .orderDate(LocalDateTime.now())
                .build();

        // When
        Order savedOrder = orderRepository.save(newOrder);

        // Then
        assertThat(savedOrder.getId()).isNotNull();
        assertThat(savedOrder.getTotalAmount()).isEqualTo(new BigDecimal("10000"));
        assertThat(savedOrder.getOrderDate()).isNotNull();
    }

    @Test
    public void save_주문아이템포함_저장성공() {
        // Given - 테스트에서 직접 도서 생성
        Book book = createAndSaveBook("Clean Code", "Robert C. Martin", new BigDecimal("38000"));

        Order order = Order.builder()
                .totalAmount(new BigDecimal("10000"))
                .orderDate(LocalDateTime.now())
                .build();

        OrderItem orderItem = OrderItem.builder()
                .book(book)
                .quantity(1)
                .price(book.getPrice())
                .build();

        order.addOrderItem(orderItem);

        // When
        Order savedOrder = orderRepository.save(order);
        entityManager.flush();
        entityManager.clear();

        // Then
        Order foundOrder = entityManager.find(Order.class, savedOrder.getId());
        assertThat(foundOrder).isNotNull();
        assertThat(foundOrder.getOrderItems()).hasSize(1);
        assertThat(foundOrder.getOrderItems().get(0).getBook().getTitle()).isEqualTo("Clean Code");
    }

    @Test
    public void findById_존재하는주문_주문반환() {
        // Given
        Order savedOrder = entityManager.persist(
                Order.builder()
                        .totalAmount(new BigDecimal("10000"))
                        .orderDate(LocalDateTime.now())
                        .build()
        );
        entityManager.flush();

        // When
        Optional<Order> foundOrder = orderRepository.findById(savedOrder.getId());

        // Then
        assertThat(foundOrder).isPresent();
        assertThat(foundOrder.get().getTotalAmount()).isEqualTo(new BigDecimal("10000"));
    }

    @Test
    public void findById_존재하지않는주문_빈Optional반환() {
        // When
        Optional<Order> foundOrder = orderRepository.findById(999L);

        // Then
        assertThat(foundOrder).isEmpty();
    }

    @Test
    public void findAll_주문목록반환() {
        // Given
        entityManager.persist(
                Order.builder()
                        .totalAmount(new BigDecimal("10000"))
                        .orderDate(LocalDateTime.now())
                        .build()
        );
        entityManager.persist(
                Order.builder()
                        .totalAmount(new BigDecimal("20000"))
                        .orderDate(LocalDateTime.now())
                        .build()
        );
        entityManager.flush();

        // When
        List<Order> orders = orderRepository.findAll();

        // Then
        assertThat(orders).isNotEmpty();
        assertThat(orders).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    public void findOrderById_존재하는주문_주문직접반환() {
        // Given
        Order savedOrder = entityManager.persist(
                Order.builder()
                        .totalAmount(new BigDecimal("10000"))
                        .orderDate(LocalDateTime.now())
                        .build()
        );
        entityManager.flush();

        // When
        Order foundOrder = orderRepository.findOrderById(savedOrder.getId());

        // Then
        assertThat(foundOrder).isNotNull();
        assertThat(foundOrder.getTotalAmount()).isEqualTo(new BigDecimal("10000"));
    }

    @Test
    public void findOrderById_존재하지않는주문_null반환() {
        // When
        Order foundOrder = orderRepository.findOrderById(999L);

        // Then
        assertThat(foundOrder).isNull();
    }

}
