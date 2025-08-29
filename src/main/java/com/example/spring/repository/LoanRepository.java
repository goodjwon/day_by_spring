package com.example.spring.repository;

import com.example.spring.entity.Loan;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LoanRepository {
    Optional<Loan> findById(Long id);
    List<Loan> findAll();
    Loan save(Loan loan);
    void deleteById(Long id);
    
    List<Loan> findByMemberId(Long memberId);
    List<Loan> findByBookId(Long bookId);
    List<Loan> findByMemberIdAndReturnDateIsNull(Long memberId);
    List<Loan> findByBookIdAndReturnDateIsNull(Long bookId);
    List<Loan> findOverdueLoans(LocalDateTime currentDate);
    List<Loan> findByReturnDateIsNull();
    
    // 편의 메서드 - 현재 시간 기준 연체 대여 조회
    default List<Loan> findOverdueLoans() {
        return findOverdueLoans(LocalDateTime.now());
    }
    List<Loan> findByLoanDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    boolean existsByBookIdAndReturnDateIsNull(Long bookId);
    
    // 연체 대여 수 조회
    long countOverdueLoans();

    default Loan findLoanById(Long id) {
        return findById(id).orElse(null);
    }
}