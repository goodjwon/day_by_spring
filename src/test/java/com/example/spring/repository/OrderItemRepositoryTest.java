package com.example.spring.repository;

import com.example.spring.entity.Book;
import com.example.spring.entity.Order;
import com.example.spring.entity.OrderItem;
import com.example.spring.entity.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(com.example.spring.repository.impl.JpaOrderItemRepository.class)
public class OrderItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderItemRepository orderItemRepository;

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

    private Order createAndSaveOrder(BigDecimal totalAmount) {
        Order order = Order.builder()
                .totalAmount(totalAmount)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .build();
        return entityManager.persistAndFlush(order);
    }

    @Test
    public void save_신규주문상품_저장성공() {
        // Given
        Book book = createAndSaveBook("자바 프로그래밍", "김저자", new BigDecimal("25000"));
        Order order = createAndSaveOrder(new BigDecimal("25000"));
        
        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .book(book)
                .quantity(1)
                .price(new BigDecimal("25000"))
                .build();

        // When
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);
        entityManager.flush();

        // Then
        assertThat(savedOrderItem.getId()).isNotNull();
        assertThat(savedOrderItem.getOrder()).isEqualTo(order);
        assertThat(savedOrderItem.getBook()).isEqualTo(book);
        assertThat(savedOrderItem.getQuantity()).isEqualTo(1);
        assertThat(savedOrderItem.getPrice()).isEqualTo(new BigDecimal("25000"));
    }

    @Test
    public void findById_존재하는주문상품_주문상품반환() {
        // Given
        Book book = createAndSaveBook("스프링 부트", "박저자", new BigDecimal("30000"));
        Order order = createAndSaveOrder(new BigDecimal("30000"));
        
        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .book(book)
                .quantity(1)
                .price(new BigDecimal("30000"))
                .build();
        OrderItem savedOrderItem = entityManager.persistAndFlush(orderItem);

        // When
        Optional<OrderItem> foundOrderItem = orderItemRepository.findById(savedOrderItem.getId());

        // Then
        assertThat(foundOrderItem).isPresent();
        assertThat(foundOrderItem.get().getBook().getTitle()).isEqualTo("스프링 부트");
        assertThat(foundOrderItem.get().getQuantity()).isEqualTo(1);
    }

    @Test
    public void findById_존재하지않는주문상품_빈Optional반환() {
        // When
        Optional<OrderItem> foundOrderItem = orderItemRepository.findById(999L);

        // Then
        assertThat(foundOrderItem).isEmpty();
    }

    @Test
    public void findByOrderId_주문별상품조회() {
        // Given
        Book book1 = createAndSaveBook("도서1", "저자1", new BigDecimal("20000"));
        Book book2 = createAndSaveBook("도서2", "저자2", new BigDecimal("15000"));
        Order order1 = createAndSaveOrder(new BigDecimal("35000"));
        Order order2 = createAndSaveOrder(new BigDecimal("20000"));

        OrderItem orderItem1 = OrderItem.builder()
                .order(order1)
                .book(book1)
                .quantity(1)
                .price(new BigDecimal("20000"))
                .build();
        
        OrderItem orderItem2 = OrderItem.builder()
                .order(order1)
                .book(book2)
                .quantity(1)
                .price(new BigDecimal("15000"))
                .build();
        
        OrderItem orderItem3 = OrderItem.builder()
                .order(order2)
                .book(book1)
                .quantity(1)
                .price(new BigDecimal("20000"))
                .build();

        entityManager.persistAndFlush(orderItem1);
        entityManager.persistAndFlush(orderItem2);
        entityManager.persistAndFlush(orderItem3);

        // When
        List<OrderItem> order1Items = orderItemRepository.findByOrderId(order1.getId());
        List<OrderItem> order2Items = orderItemRepository.findByOrderId(order2.getId());

        // Then
        assertThat(order1Items).hasSize(2);
        assertThat(order2Items).hasSize(1);
        assertThat(order1Items).allMatch(item -> item.getOrder().getId().equals(order1.getId()));
        assertThat(order2Items).allMatch(item -> item.getOrder().getId().equals(order2.getId()));
    }

    @Test
    public void findByBookId_도서별주문내역조회() {
        // Given
        Book popularBook = createAndSaveBook("인기도서", "유명저자", new BigDecimal("25000"));
        Book normalBook = createAndSaveBook("일반도서", "일반저자", new BigDecimal("20000"));
        Order order1 = createAndSaveOrder(new BigDecimal("25000"));
        Order order2 = createAndSaveOrder(new BigDecimal("25000"));

        OrderItem orderItem1 = OrderItem.builder()
                .order(order1)
                .book(popularBook)
                .quantity(1)
                .price(new BigDecimal("25000"))
                .build();
        
        OrderItem orderItem2 = OrderItem.builder()
                .order(order2)
                .book(popularBook)
                .quantity(1)
                .price(new BigDecimal("25000"))
                .build();
        
        OrderItem orderItem3 = OrderItem.builder()
                .order(order1)
                .book(normalBook)
                .quantity(1)
                .price(new BigDecimal("20000"))
                .build();

        entityManager.persistAndFlush(orderItem1);
        entityManager.persistAndFlush(orderItem2);
        entityManager.persistAndFlush(orderItem3);

        // When
        List<OrderItem> popularBookOrders = orderItemRepository.findByBookId(popularBook.getId());
        List<OrderItem> normalBookOrders = orderItemRepository.findByBookId(normalBook.getId());

        // Then
        assertThat(popularBookOrders).hasSize(2);
        assertThat(normalBookOrders).hasSize(1);
        assertThat(popularBookOrders).allMatch(item -> item.getBook().getId().equals(popularBook.getId()));
    }

    @Test
    public void findByOrderIdAndBookId_특정주문의특정도서조회() {
        // Given
        Book book1 = createAndSaveBook("도서1", "저자1", new BigDecimal("20000"));
        Book book2 = createAndSaveBook("도서2", "저자2", new BigDecimal("15000"));
        Order order = createAndSaveOrder(new BigDecimal("35000"));

        OrderItem orderItem1 = OrderItem.builder()
                .order(order)
                .book(book1)
                .quantity(2)
                .price(new BigDecimal("20000"))
                .build();
        
        OrderItem orderItem2 = OrderItem.builder()
                .order(order)
                .book(book2)
                .quantity(1)
                .price(new BigDecimal("15000"))
                .build();

        entityManager.persistAndFlush(orderItem1);
        entityManager.persistAndFlush(orderItem2);

        // When
        List<OrderItem> specificOrderItems = orderItemRepository.findByOrderIdAndBookId(order.getId(), book1.getId());

        // Then
        assertThat(specificOrderItems).hasSize(1);
        assertThat(specificOrderItems.get(0).getBook().getTitle()).isEqualTo("도서1");
        assertThat(specificOrderItems.get(0).getQuantity()).isEqualTo(2);
    }

    @Test
    public void findAll_전체주문상품조회() {
        // Given
        Book book = createAndSaveBook("테스트도서", "테스트저자", new BigDecimal("10000"));
        Order order1 = createAndSaveOrder(new BigDecimal("10000"));
        Order order2 = createAndSaveOrder(new BigDecimal("10000"));

        OrderItem orderItem1 = OrderItem.builder()
                .order(order1)
                .book(book)
                .quantity(1)
                .price(new BigDecimal("10000"))
                .build();
        
        OrderItem orderItem2 = OrderItem.builder()
                .order(order2)
                .book(book)
                .quantity(1)
                .price(new BigDecimal("10000"))
                .build();

        entityManager.persistAndFlush(orderItem1);
        entityManager.persistAndFlush(orderItem2);

        // When
        List<OrderItem> allOrderItems = orderItemRepository.findAll();

        // Then
        assertThat(allOrderItems).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    public void deleteById_주문상품삭제() {
        // Given
        Book book = createAndSaveBook("삭제테스트도서", "삭제테스트저자", new BigDecimal("10000"));
        Order order = createAndSaveOrder(new BigDecimal("10000"));
        
        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .book(book)
                .quantity(1)
                .price(new BigDecimal("10000"))
                .build();
        OrderItem savedOrderItem = entityManager.persistAndFlush(orderItem);
        Long orderItemId = savedOrderItem.getId();

        // When
        orderItemRepository.deleteById(orderItemId);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<OrderItem> deletedOrderItem = orderItemRepository.findById(orderItemId);
        assertThat(deletedOrderItem).isEmpty();
    }

    @Test
    public void findOrderItemById_편의메서드_주문상품직접반환() {
        // Given
        Book book = createAndSaveBook("편의메서드테스트", "테스트저자", new BigDecimal("10000"));
        Order order = createAndSaveOrder(new BigDecimal("10000"));
        
        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .book(book)
                .quantity(1)
                .price(new BigDecimal("10000"))
                .build();
        OrderItem savedOrderItem = entityManager.persistAndFlush(orderItem);

        // When
        OrderItem foundOrderItem = orderItemRepository.findOrderItemById(savedOrderItem.getId());

        // Then
        assertThat(foundOrderItem).isNotNull();
        assertThat(foundOrderItem.getId()).isEqualTo(savedOrderItem.getId());
        assertThat(foundOrderItem.getBook().getTitle()).isEqualTo("편의메서드테스트");
    }

    @Test
    public void findOrderItemById_존재하지않는주문상품_null반환() {
        // When
        OrderItem foundOrderItem = orderItemRepository.findOrderItemById(999L);

        // Then
        assertThat(foundOrderItem).isNull();
    }
}