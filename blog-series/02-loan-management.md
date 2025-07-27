# [연재 2편] Spring Boot로 배우는 대여 관리 시스템: 복잡한 비즈니스 로직 정복하기

> **연재 목차**: [1편 회원관리](./01-member-management.md) → **2편 대여관리** → [3편 도서관리](./03-book-management.md) → [4편 주문관리](./04-order-management.md) → [5편 고급기능](./05-advanced-features.md)

## 🎯 2편에서 다룰 내용

1편에서 견고한 회원 관리 시스템을 구축했다면, 이번 편에서는 **더욱 복잡한 비즈니스 로직**을 가진 대여 관리 시스템을 구현해보겠습니다.

### 📋 구현할 핵심 기능
- ✅ **도서 대여/반납** 처리
- ✅ **연체 관리** 및 벌금 계산
- ✅ **예약 시스템** 구현
- ✅ **대여 히스토리** 관리
- ✅ **자동화된 알림** 시스템

### 🔧 핵심 학습 기술
- **State Pattern**: 복잡한 대여 상태 관리
- **Saga Pattern**: 분산 트랜잭션 처리
- **Batch Processing**: 연체 자동 처리
- **복잡한 비즈니스 규칙** 구현

---

## 🚧 준비 중입니다!

이 편은 현재 작성 중입니다. 곧 업데이트될 예정이니 조금만 기다려주세요! 

### 🔔 알림 신청
2편이 완성되면 알림을 받고 싶으시다면:
1. 이 저장소를 **Watch** 해주세요
2. **Star**⭐를 눌러 응원해주세요

### 📝 미리 준비할 것들
1편의 **MemberService**가 완성되어 있어야 합니다:
- [x] 회원 가입/조회 기능
- [x] 멤버십 관리 (REGULAR/PREMIUM)
- [x] 대여 제한 정보 조회

---

## 🎯 다음 편 미리보기

### LoanService 핵심 메서드
```java
public interface LoanService {
    // 도서 대여
    LoanResponse loanBook(LoanRequest request);
    
    // 도서 반납
    ReturnResponse returnBook(Long loanId, LocalDateTime returnDate);
    
    // 연체 도서 조회
    List<OverdueLoanResponse> findOverdueLoans();
    
    // 예약 처리
    ReservationResponse reserveBook(ReservationRequest request);
    
    // 대여 히스토리 조회
    List<LoanHistoryResponse> getLoanHistory(Long memberId, Pageable pageable);
}
```

### 상태 관리 예시
```java
@Getter
public enum LoanStatus {
    ACTIVE("대여중"),
    RETURNED("반납완료"),
    OVERDUE("연체"),
    LOST("분실");
    
    private final String description;
}
```

---

## 🔗 관련 링크

- **[1편: 회원 관리 시스템](./01-member-management.md)** - 기본기 다지기
- **[연재 메인 페이지](./README.md)** - 전체 로드맵 확인
- **[GitHub 저장소](https://github.com/example/day-by-spring)** - 전체 코드 확인

---

**💡 이 연재가 도움이 되셨다면 ⭐Star를 눌러주세요!**