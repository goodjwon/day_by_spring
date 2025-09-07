package com.example.spring.service.impl;

import com.example.spring.entity.Book;
import com.example.spring.exception.BookException;
import com.example.spring.exception.EntityNotFoundException;
import com.example.spring.repository.BookRepository;
import com.example.spring.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * BookService 구현체
 * 도서 관리 비즈니스 로직을 구현합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Override
    @Transactional
    public Book createBook(Book book) {
        log.info("도서 생성 요청 - ISBN: {}, 제목: {}", book.getIsbn(), book.getTitle());
        
        validateBook(book);
        
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new BookException.DuplicateIsbnException("이미 존재하는 ISBN입니다: " + book.getIsbn());
        }
        
        book.setCreatedDate(LocalDateTime.now());
        Book savedBook = bookRepository.save(book);
        
        log.info("도서 생성 완료 - ID: {}, ISBN: {}", savedBook.getId(), savedBook.getIsbn());
        return savedBook;
    }

    @Override
    public Optional<Book> getBookById(Long id) {
        log.debug("도서 ID 조회 - ID: {}", id);
        return bookRepository.findById(id)
                .filter(book -> book.getDeletedDate() == null);
    }

    @Override
    public Optional<Book> getBookByIsbn(String isbn) {
        log.debug("ISBN으로 도서 조회 - ISBN: {}", isbn);
        return bookRepository.findByIsbn(isbn)
                .filter(book -> book.getDeletedDate() == null);
    }

    @Override
    public List<Book> getAllActiveBooks() {
        log.debug("모든 활성 도서 조회");
        return bookRepository.findByDeletedDateIsNull();
    }

    @Override
    @Transactional
    public Book updateBook(Long id, Book updatedBook) {
        log.info("도서 정보 수정 요청 - ID: {}", id);
        
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("도서를 찾을 수 없습니다: " + id));
        
        if (existingBook.getDeletedDate() != null) {
            throw new BookException.DeletedBookAccessException("삭제된 도서는 수정할 수 없습니다: " + id);
        }
        
        validateBook(updatedBook);
        
        // ISBN 중복 검사 (자기 자신 제외)
        if (!existingBook.getIsbn().equals(updatedBook.getIsbn()) && 
            bookRepository.existsByIsbn(updatedBook.getIsbn())) {
            throw new BookException.DuplicateIsbnException("이미 존재하는 ISBN입니다: " + updatedBook.getIsbn());
        }
        
        // 필드 업데이트
        existingBook.setTitle(updatedBook.getTitle());
        existingBook.setAuthor(updatedBook.getAuthor());
        existingBook.setIsbn(updatedBook.getIsbn());
        existingBook.setPrice(updatedBook.getPrice());
        existingBook.setAvailable(updatedBook.getAvailable());
        existingBook.setUpdatedDate(LocalDateTime.now());
        
        Book savedBook = bookRepository.save(existingBook);
        
        log.info("도서 정보 수정 완료 - ID: {}", savedBook.getId());
        return savedBook;
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        log.info("도서 삭제 요청 - ID: {}", id);
        
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("도서를 찾을 수 없습니다: " + id));
        
        if (book.getDeletedDate() != null) {
            throw new BookException.DeletedBookAccessException("이미 삭제된 도서입니다: " + id);
        }
        
        book.markAsDeleted();
        bookRepository.save(book);
        
        log.info("도서 삭제 완료 - ID: {}", id);
    }

    @Override
    @Transactional
    public void restoreBook(Long id) {
        log.info("도서 복원 요청 - ID: {}", id);
        
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("도서를 찾을 수 없습니다: " + id));
        
        if (book.getDeletedDate() == null) {
            throw new BookException.InvalidBookStateException("삭제되지 않은 도서는 복원할 수 없습니다: " + id);
        }
        
        book.restore();
        bookRepository.save(book);
        
        log.info("도서 복원 완료 - ID: {}", id);
    }

    @Override
    public List<Book> searchByTitle(String title) {
        log.debug("제목으로 도서 검색 - 키워드: {}", title);
        if (!StringUtils.hasText(title)) {
            return List.of();
        }
        return bookRepository.findByTitleContaining(title).stream()
                .filter(book -> book.getDeletedDate() == null)
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> searchByAuthor(String author) {
        log.debug("저자로 도서 검색 - 키워드: {}", author);
        if (!StringUtils.hasText(author)) {
            return List.of();
        }
        return bookRepository.findByAuthorContaining(author).stream()
                .filter(book -> book.getDeletedDate() == null)
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> searchByKeyword(String keyword) {
        log.debug("키워드로 도서 검색 - 키워드: {}", keyword);
        if (!StringUtils.hasText(keyword)) {
            return List.of();
        }
        return bookRepository.findByTitleContainingOrAuthorContaining(keyword, keyword).stream()
                .filter(book -> book.getDeletedDate() == null)
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> searchByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        log.debug("가격 범위로 도서 검색 - 최소: {}, 최대: {}", minPrice, maxPrice);
        
        if (minPrice == null && maxPrice == null) {
            return getAllActiveBooks();
        }
        
        if (minPrice == null) {
            minPrice = BigDecimal.ZERO;
        }
        if (maxPrice == null) {
            maxPrice = new BigDecimal("999999.99");
        }
        
        if (minPrice.compareTo(maxPrice) > 0) {
            throw new BookException.InvalidPriceRangeException("최소 가격이 최대 가격보다 클 수 없습니다");
        }
        
        return bookRepository.findByPriceBetween(minPrice, maxPrice).stream()
                .filter(book -> book.getDeletedDate() == null)
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> searchBooksWithFilters(String title, String author, 
                                           BigDecimal minPrice, BigDecimal maxPrice, 
                                           Boolean available) {
        log.debug("복합 조건으로 도서 검색 - 제목: {}, 저자: {}, 최소가격: {}, 최대가격: {}, 재고: {}", 
                title, author, minPrice, maxPrice, available);
        
        List<Book> books = getAllActiveBooks();
        
        return books.stream()
                .filter(book -> title == null || book.getTitle().toLowerCase().contains(title.toLowerCase()))
                .filter(book -> author == null || book.getAuthor().toLowerCase().contains(author.toLowerCase()))
                .filter(book -> minPrice == null || book.getPrice().compareTo(minPrice) >= 0)
                .filter(book -> maxPrice == null || book.getPrice().compareTo(maxPrice) <= 0)
                .filter(book -> available == null || book.getAvailable().equals(available))
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> getBooksByAvailability(Boolean available) {
        log.debug("재고 상태별 도서 조회 - 재고: {}", available);
        return bookRepository.findByAvailable(available).stream()
                .filter(book -> book.getDeletedDate() == null)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isIsbnExists(String isbn) {
        log.debug("ISBN 중복 검증 - ISBN: {}", isbn);
        return bookRepository.existsByIsbn(isbn);
    }

    @Override
    @Transactional
    public Book updateBookAvailability(Long id, Boolean available) {
        log.info("도서 재고 상태 업데이트 - ID: {}, 재고: {}", id, available);
        
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("도서를 찾을 수 없습니다: " + id));
        
        if (book.getDeletedDate() != null) {
            throw new BookException.DeletedBookAccessException("삭제된 도서의 재고를 수정할 수 없습니다: " + id);
        }
        
        book.setAvailable(available);
        book.setUpdatedDate(LocalDateTime.now());
        
        Book savedBook = bookRepository.save(book);
        
        log.info("도서 재고 상태 업데이트 완료 - ID: {}, 재고: {}", id, available);
        return savedBook;
    }

    @Override
    public long getTotalBooksCount() {
        log.debug("전체 도서 수 조회");
        return bookRepository.findAll().size();
    }

    @Override
    public long getActiveBooksCount() {
        log.debug("활성 도서 수 조회");
        return bookRepository.findByDeletedDateIsNull().size();
    }

    /**
     * 도서 유효성 검증
     */
    private void validateBook(Book book) {
        if (book == null) {
            throw new BookException.InvalidBookDataException("도서 정보가 null입니다");
        }
        if (!StringUtils.hasText(book.getTitle())) {
            throw new BookException.InvalidBookDataException("도서 제목은 필수입니다");
        }
        if (!StringUtils.hasText(book.getAuthor())) {
            throw new BookException.InvalidBookDataException("저자는 필수입니다");
        }
        if (!StringUtils.hasText(book.getIsbn())) {
            throw new BookException.InvalidBookDataException("ISBN은 필수입니다");
        }
        if (book.getPrice() == null || book.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new BookException.InvalidBookDataException("가격은 0 이상이어야 합니다");
        }
    }
}