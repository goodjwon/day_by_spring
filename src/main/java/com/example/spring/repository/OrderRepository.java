package com.example.spring.repository;

/**
 * 주문 레포지토리 마커 인터페이스
 * 실제 메서드는 JpaOrderRepository (extends JpaRepository)에서 제공
 */
public interface OrderRepository {
    // JpaRepository가 모든 CRUD 메서드를 제공하므로 여기서는 선언하지 않음
}