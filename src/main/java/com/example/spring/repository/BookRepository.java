package com.example.spring.repository;

import com.example.spring.entity.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository {
    Optional<Book> findById(Long id);
    List<Book> findAll();
    Book save(Book book);
    void deleteById(Long id);

    // 편의 메서드 - Optional을 처리하지 않고 직접 Book 반환
    default Book findBookById(Long id) {
        return findById(id).orElse(null);
    }
}
