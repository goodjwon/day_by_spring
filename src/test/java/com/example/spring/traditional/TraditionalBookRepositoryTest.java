package com.example.spring.traditional;

import com.example.spring.entity.Book;
import org.junit.jupiter.api.Test;        // JUnit 5로 변경
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TraditionalBookRepositoryTest {

    private TraditionalBookRepository repository;

    @BeforeEach
    void setUp() {
        repository = new TraditionalBookRepository();
    }

    @AfterEach
    void tearDown() {
        if (repository != null) {
            repository.close();
        }
    }

    @Test
    public void findById_정상조회() {
        // When
        Book book = repository.findById(1L);

        // Then
        assertNotNull(book);
        assertEquals(1L, book.getId());
    }

    @Test
    public void findById_존재하지않음() {
        // When
        Book book = repository.findById(999L);

        // Then
        assertNull(book);
    }

    @Test
    public void findAll_전체조회() {
        // When
        List<Book> books = repository.findAll();

        // Then
        assertNotNull(books);
        assertFalse(books.isEmpty());
    }
}