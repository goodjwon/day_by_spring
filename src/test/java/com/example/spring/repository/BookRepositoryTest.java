package com.example.spring.repository;

import com.example.spring.entity.Book;
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
public class BookRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookRepository bookRepository;

    @Test
    public void findById_존재하는도서_도서반환() {
        // Given - 데이터베이스에 이미 존재하는 도서 사용 (ID: 1)
        Long bookId = 1L;

        // When
        Optional<Book> foundBook = bookRepository.findById(bookId);

        // Then
        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getTitle()).isEqualTo("Clean Code");
        assertThat(foundBook.get().getAuthor()).isEqualTo("Robert C. Martin");
    }

    @Test
    public void findById_존재하지않는도서_빈Optional반환() {
        // When
        Optional<Book> foundBook = bookRepository.findById(999L);

        // Then
        assertThat(foundBook).isEmpty();
    }

    @Test
    public void findAll_도서목록반환() {
        // When
        List<Book> books = bookRepository.findAll();

        // Then
        assertThat(books).isNotEmpty();
        assertThat(books).hasSizeGreaterThanOrEqualTo(5); // 데이터베이스에 이미 5개의 도서가 있음
    }

    @Test
    public void save_신규도서_저장성공() {
        // Given
        Book newBook = Book.builder()
                .title("새 도서")
                .author("새 저자")
                .price(new BigDecimal("10000"))
                .isbn("1234567890" + System.currentTimeMillis()) // 유니크한 ISBN 생성
                .available(true)
                .createdDate(LocalDateTime.now())
                .build();

        // When
        Book savedBook = bookRepository.save(newBook);
        entityManager.flush(); // 즉시 저장

        // Then
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("새 도서");
        assertThat(savedBook.getAuthor()).isEqualTo("새 저자");

        // 테스트 후 정리
        bookRepository.deleteById(savedBook.getId());
    }

    @Test
    public void save_기존도서_업데이트성공() {
        // Given - 데이터베이스에 이미 존재하는 도서 사용 (ID: 2)
        Book existingBook = bookRepository.findById(2L).get();
        String originalTitle = existingBook.getTitle();
        String originalAuthor = existingBook.getAuthor();

        // When
        existingBook.setTitle("수정된 도서");
        existingBook.setAuthor("수정된 저자");
        Book updatedBook = bookRepository.save(existingBook);
        entityManager.flush();
        entityManager.clear();

        // Then
        Book reloadedBook = bookRepository.findById(2L).get();
        assertThat(reloadedBook.getTitle()).isEqualTo("수정된 도서");
        assertThat(reloadedBook.getAuthor()).isEqualTo("수정된 저자");

        // Restore original values for other tests
        reloadedBook.setTitle(originalTitle);
        reloadedBook.setAuthor(originalAuthor);
        bookRepository.save(reloadedBook);
    }

    @Test
    public void deleteById_도서삭제() {
        // Given - 새로운 도서 생성 (기존 데이터 삭제 방지)
        Book newBook = Book.builder()
                .title("삭제할 도서")
                .author("삭제할 저자")
                .price(new BigDecimal("10000"))
                .isbn("9876543210" + System.currentTimeMillis()) // 유니크한 ISBN 생성
                .available(true)
                .createdDate(LocalDateTime.now())
                .build();
        Book savedBook = bookRepository.save(newBook);
        entityManager.flush();
        Long bookId = savedBook.getId();

        // When
        bookRepository.deleteById(bookId);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<Book> deletedBook = bookRepository.findById(bookId);
        assertThat(deletedBook).isEmpty();
    }

    @Test
    public void findBookById_존재하는도서_도서직접반환() {
        // Given - 데이터베이스에 이미 존재하는 도서 사용 (ID: 3)
        Long bookId = 3L;

        // When
        Book foundBook = bookRepository.findBookById(bookId);

        // Then
        assertThat(foundBook).isNotNull();
        assertThat(foundBook.getTitle()).isEqualTo("Effective Java");
        assertThat(foundBook.getAuthor()).isEqualTo("Joshua Bloch");
    }

    @Test
    public void findBookById_존재하지않는도서_null반환() {
        // When
        Book foundBook = bookRepository.findBookById(999L);

        // Then
        assertThat(foundBook).isNull();
    }
}
