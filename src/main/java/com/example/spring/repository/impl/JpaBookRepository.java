package com.example.spring.repository.impl;

import com.example.spring.entity.Book;
import com.example.spring.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA를 사용한 Repository 구현
 * - 간단한 인터페이스 선언만으로 CRUD 기능 자동 생성
 * - Query Methods를 통한 자동 쿼리 생성
 * - Soft Delete 패턴 지원
 */
@Repository
public interface JpaBookRepository extends JpaRepository<Book, Long>, BookRepository {
    
    // ISBN 관련 메서드 - JPA Query Methods
    Optional<Book> findByIsbn(String isbn);
    boolean existsByIsbn(String isbn);

    // 검색 메서드 (Soft Delete 고려)
    List<Book> findByTitleContainingIgnoreCaseAndDeletedDateIsNull(String title);
    List<Book> findByAuthorContainingIgnoreCaseAndDeletedDateIsNull(String author);
    
    @Query("SELECT b FROM Book b WHERE (UPPER(b.title) LIKE UPPER(CONCAT('%', :title, '%')) " +
           "OR UPPER(b.author) LIKE UPPER(CONCAT('%', :author, '%'))) " +
           "AND b.deletedDate IS NULL")
    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseAndDeletedDateIsNull(
        @Param("title") String title, @Param("author") String author);

    // 가격 범위 검색
    List<Book> findByPriceBetweenAndDeletedDateIsNull(BigDecimal minPrice, BigDecimal maxPrice);

    // 삭제되지 않은 도서만 조회
    List<Book> findByDeletedDateIsNull();
    Page<Book> findByDeletedDateIsNull(Pageable pageable);

    // 재고 상태별 조회
    List<Book> findByAvailableAndDeletedDateIsNull(Boolean available);

    // 복합 검색 쿼리
    @Query("SELECT b FROM Book b WHERE " +
           "(:title IS NULL OR UPPER(b.title) LIKE UPPER(CONCAT('%', :title, '%'))) " +
           "AND (:author IS NULL OR UPPER(b.author) LIKE UPPER(CONCAT('%', :author, '%'))) " +
           "AND (:minPrice IS NULL OR b.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR b.price <= :maxPrice) " +
           "AND (:available IS NULL OR b.available = :available) " +
           "AND b.deletedDate IS NULL " +
           "ORDER BY b.createdDate DESC")
    Page<Book> findBooksWithFilters(
        @Param("title") String title,
        @Param("author") String author,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        @Param("available") Boolean available,
        Pageable pageable);
}
