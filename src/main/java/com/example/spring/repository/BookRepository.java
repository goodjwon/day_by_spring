package com.example.spring.repository;

import com.example.spring.entity.Book;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookRepository {
    Optional<Book> findById(Long id);
    List<Book> findAll();
    Book save(Book book);
    void deleteById(Long id);
    
    // ISBN 관련 메서드
    Optional<Book> findByIsbn(String isbn);
    boolean existsByIsbn(String isbn);
    
    // 제목/저자 검색 메서드
    List<Book> findByTitleContaining(String title);
    List<Book> findByAuthorContaining(String author);
    List<Book> findByTitleContainingOrAuthorContaining(String title, String author);
    
    // 가격 범위 검색
    List<Book> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    // 재고 상태별 조회
    List<Book> findByAvailable(Boolean available);
    
    // 복합 조건 검색
    List<Book> findByAvailableAndTitleContaining(Boolean available, String title);
    List<Book> findByAvailableAndAuthorContaining(Boolean available, String author);
    List<Book> findByAvailableAndPriceBetween(Boolean available, BigDecimal minPrice, BigDecimal maxPrice);
    
    // 날짜 범위 검색
    List<Book> findByCreatedDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Soft Delete 관련 메서드
    List<Book> findByDeletedDateIsNull();
    List<Book> findByDeletedDateIsNotNull();
    List<Book> findByDeletedDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // 복합 조건 검색 (JPQL 버전 - 페이징)
    List<Book> searchBooksWithQueryFilters(String title, String author,
                                          BigDecimal minPrice, BigDecimal maxPrice,
                                          Boolean available, int offset, int limit);

    long countBooksWithQueryFilters(String title, String author,
                                   BigDecimal minPrice, BigDecimal maxPrice,
                                   Boolean available);

    // 편의 메서드
    default Book findBookById(Long id) {
        return findById(id).orElse(null);
    }

    default List<Book> findActiveBooks() {
        return findByDeletedDateIsNull();
    }

    default List<Book> searchBooks(String keyword) {
        return findByTitleContainingOrAuthorContaining(keyword, keyword);
    }
}
