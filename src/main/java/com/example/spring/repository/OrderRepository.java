package com.example.spring.repository;

/**
 * 주문 레포지토리 마커 인터페이스
 * 실제 메서드는 JpaOrderRepository (extends JpaRepository)에서 제공
 *
 * Note: 이 인터페이스는 기존 테스트 코드와의 호환성을 위해 유지됩니다.
 * 새로운 코드에서는 JpaOrderRepository를 직접 사용하는 것을 권장합니다.
 */
public interface OrderRepository {
    // JpaRepository가 모든 CRUD 메서드를 제공하므로 여기서는 선언하지 않음
}
