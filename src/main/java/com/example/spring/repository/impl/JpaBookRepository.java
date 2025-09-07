package com.example.spring.repository.impl;

import com.example.spring.entity.Book;
import com.example.spring.repository.BookRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * EntityManager를 사용한 BookRepository JPA 구현
 * - Member Repository와 동일한 패턴 적용
 * - JPQL을 사용한 쿼리 구현
 * - Soft Delete 패턴 지원
 */
@Repository
@Transactional
public class JpaBookRepository implements BookRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Book> findById(Long id) {
        Book book = em.find(Book.class, id);
        return Optional.ofNullable(book);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findAll() {
        return em.createQuery("SELECT b FROM Book b ORDER BY b.createdDate DESC", Book.class)
                .getResultList();
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == null) {
            em.persist(book);
            return book;
        } else {
            return em.merge(book);
        }
    }

    @Override
    public void deleteById(Long id) {
        Book book = em.find(Book.class, id);
        if (book != null) {
            em.remove(book);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Book> findByIsbn(String isbn) {
        TypedQuery<Book> query = em.createQuery(
                "SELECT b FROM Book b WHERE b.isbn = :isbn", Book.class);
        query.setParameter("isbn", isbn);
        
        List<Book> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByIsbn(String isbn) {
        Long count = em.createQuery(
                "SELECT COUNT(b) FROM Book b WHERE b.isbn = :isbn", Long.class)
                .setParameter("isbn", isbn)
                .getSingleResult();
        return count > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findByTitleContaining(String title) {
        return em.createQuery(
                "SELECT b FROM Book b WHERE b.title LIKE :title ORDER BY b.title", 
                Book.class)
                .setParameter("title", "%" + title + "%")
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findByAuthorContaining(String author) {
        return em.createQuery(
                "SELECT b FROM Book b WHERE b.author LIKE :author ORDER BY b.author", 
                Book.class)
                .setParameter("author", "%" + author + "%")
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findByTitleContainingOrAuthorContaining(String title, String author) {
        return em.createQuery(
                "SELECT b FROM Book b WHERE b.title LIKE :title OR b.author LIKE :author ORDER BY b.title", 
                Book.class)
                .setParameter("title", "%" + title + "%")
                .setParameter("author", "%" + author + "%")
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return em.createQuery(
                "SELECT b FROM Book b WHERE b.price BETWEEN :minPrice AND :maxPrice ORDER BY b.price", 
                Book.class)
                .setParameter("minPrice", minPrice)
                .setParameter("maxPrice", maxPrice)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findByAvailable(Boolean available) {
        return em.createQuery(
                "SELECT b FROM Book b WHERE b.available = :available ORDER BY b.createdDate DESC", 
                Book.class)
                .setParameter("available", available)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findByAvailableAndTitleContaining(Boolean available, String title) {
        return em.createQuery(
                "SELECT b FROM Book b WHERE b.available = :available AND b.title LIKE :title ORDER BY b.title", 
                Book.class)
                .setParameter("available", available)
                .setParameter("title", "%" + title + "%")
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findByAvailableAndAuthorContaining(Boolean available, String author) {
        return em.createQuery(
                "SELECT b FROM Book b WHERE b.available = :available AND b.author LIKE :author ORDER BY b.author", 
                Book.class)
                .setParameter("available", available)
                .setParameter("author", "%" + author + "%")
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findByAvailableAndPriceBetween(Boolean available, BigDecimal minPrice, BigDecimal maxPrice) {
        return em.createQuery(
                "SELECT b FROM Book b WHERE b.available = :available AND b.price BETWEEN :minPrice AND :maxPrice ORDER BY b.price", 
                Book.class)
                .setParameter("available", available)
                .setParameter("minPrice", minPrice)
                .setParameter("maxPrice", maxPrice)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findByCreatedDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return em.createQuery(
                "SELECT b FROM Book b WHERE b.createdDate BETWEEN :startDate AND :endDate ORDER BY b.createdDate DESC", 
                Book.class)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findByDeletedDateIsNull() {
        return em.createQuery(
                "SELECT b FROM Book b WHERE b.deletedDate IS NULL ORDER BY b.createdDate DESC", 
                Book.class)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findByDeletedDateIsNotNull() {
        return em.createQuery(
                "SELECT b FROM Book b WHERE b.deletedDate IS NOT NULL ORDER BY b.deletedDate DESC", 
                Book.class)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findByDeletedDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return em.createQuery(
                "SELECT b FROM Book b WHERE b.deletedDate BETWEEN :startDate AND :endDate ORDER BY b.deletedDate DESC", 
                Book.class)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
    }
}
