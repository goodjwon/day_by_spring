package com.example.spring.repository;

import com.example.spring.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BookRepository {
    // 기본 CRUD 메서드
    Optional<Book> findById(Long id);
    List<Book> findAll();
    Book save(Book book);
    void deleteById(Long id);

    // ISBN 관련 메서드
    Optional<Book> findByIsbn(String isbn);
    boolean existsByIsbn(String isbn);

    // 검색 메서드 (Soft Delete 고려)
    List<Book> findByTitleContainingIgnoreCaseAndDeletedDateIsNull(String title);
    List<Book> findByAuthorContainingIgnoreCaseAndDeletedDateIsNull(String author);
    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseAndDeletedDateIsNull(
        String title, String author);

    // 가격 범위 검색
    List<Book> findByPriceBetweenAndDeletedDateIsNull(BigDecimal minPrice, BigDecimal maxPrice);

    // 삭제되지 않은 도서만 조회
    List<Book> findByDeletedDateIsNull();
    Page<Book> findByDeletedDateIsNull(Pageable pageable);

    // 재고 상태별 조회
    List<Book> findByAvailableAndDeletedDateIsNull(Boolean available);

    // 편의 메서드
    default Book findBookById(Long id) {
        return findById(id).filter(book -> !book.isDeleted()).orElse(null);
    }

    default Optional<Book> findActiveById(Long id) {
        return findById(id).filter(book -> !book.isDeleted());
    }

    default List<Book> searchBooks(String keyword) {
        return findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseAndDeletedDateIsNull(
            keyword, keyword);
    }
}
