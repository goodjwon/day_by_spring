package com.example.spring.repository.impl;

import com.example.spring.entity.Loan;
import com.example.spring.repository.LoanRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class JpaLoanRepository implements LoanRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Loan> findById(Long id) {
        Loan loan = em.find(Loan.class, id);
        return Optional.ofNullable(loan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Loan> findAll() {
        return em.createQuery("SELECT l FROM Loan l ORDER BY l.loanDate DESC", Loan.class)
                .getResultList();
    }

    @Override
    public Loan save(Loan loan) {
        if (loan.getId() == null) {
            em.persist(loan);
            return loan;
        } else {
            return em.merge(loan);
        }
    }

    @Override
    public void deleteById(Long id) {
        Loan loan = em.find(Loan.class, id);
        if (loan != null) {
            em.remove(loan);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Loan> findByMemberId(Long memberId) {
        return em.createQuery(
                "SELECT l FROM Loan l WHERE l.member.id = :memberId ORDER BY l.loanDate DESC", 
                Loan.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Loan> findByBookId(Long bookId) {
        return em.createQuery(
                "SELECT l FROM Loan l WHERE l.book.id = :bookId ORDER BY l.loanDate DESC", 
                Loan.class)
                .setParameter("bookId", bookId)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Loan> findByMemberIdAndReturnDateIsNull(Long memberId) {
        return em.createQuery(
                "SELECT l FROM Loan l WHERE l.member.id = :memberId AND l.returnDate IS NULL ORDER BY l.loanDate DESC", 
                Loan.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Loan> findByBookIdAndReturnDateIsNull(Long bookId) {
        return em.createQuery(
                "SELECT l FROM Loan l WHERE l.book.id = :bookId AND l.returnDate IS NULL", 
                Loan.class)
                .setParameter("bookId", bookId)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Loan> findOverdueLoans(LocalDateTime currentDate) {
        return em.createQuery(
                "SELECT l FROM Loan l WHERE l.dueDate < :currentDate AND l.returnDate IS NULL ORDER BY l.dueDate", 
                Loan.class)
                .setParameter("currentDate", currentDate)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Loan> findByReturnDateIsNull() {
        return em.createQuery(
                "SELECT l FROM Loan l WHERE l.returnDate IS NULL ORDER BY l.loanDate DESC", 
                Loan.class)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Loan> findByLoanDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return em.createQuery(
                "SELECT l FROM Loan l WHERE l.loanDate BETWEEN :startDate AND :endDate ORDER BY l.loanDate DESC", 
                Loan.class)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByBookIdAndReturnDateIsNull(Long bookId) {
        Long count = em.createQuery(
                "SELECT COUNT(l) FROM Loan l WHERE l.book.id = :bookId AND l.returnDate IS NULL", 
                Long.class)
                .setParameter("bookId", bookId)
                .getSingleResult();
        return count > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public long countOverdueLoans() {
        return em.createQuery(
                "SELECT COUNT(l) FROM Loan l WHERE l.dueDate < :currentDate AND l.returnDate IS NULL", 
                Long.class)
                .setParameter("currentDate", LocalDateTime.now())
                .getSingleResult();
    }
}