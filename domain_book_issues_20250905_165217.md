# GitHub Issues Export

**Repository:** goodjwon/day_by_spring  
**Export Date:** 2025-09-05 16:52:18  
**Filter:** domain:book  

---

## 📊 이슈 통계

- **총 이슈 수:** 14
- **열린 이슈:** 0
- **닫힌 이슈:** 0

### 🏷️ 주요 라벨

- **domain:book:** 14개
- **priority:high:** 7개
- **priority:critical:** 6개
- **task:** 5개
- **sub-task:** 4개
- **story:** 4개
- **layer:entity:** 3개
- **type:validation:** 2개
- **type:testing:** 1개
- **layer:controller:** 1개

---

## 🔒 #35 [SUB-TASK] Book Entity 단위 테스트 작성

**📊 상태:** OPEN  
**👤 작성자:** goodjwon  
**📅 생성일:** 2025-08-30  
**🔄 수정일:** 2025-08-30  
**🏷️ 라벨:** `sub-task` `priority:high` `domain:book` `type:testing`  
**🔗 링크:** https://github.com/goodjwon/day_by_spring/issues/35

### 📝 설명

# 🧪 Book Entity 단위 테스트 작성

## 📋 Sub-task 정보
- **Task**: [TASK] Book Entity 설계 및 구현 (#27)
- **상태**: ⏳ 대기중
- **우선순위**: 🟡 High
- **예상 소요시간**: 0.5시간

## 📝 Sub-task 설명
Book 엔티티의 기본 동작과 검증 로직을 테스트합니다.

## 🎯 구현 세부사항
```java
@Test
class BookTest {
    
    @Test
    void 도서_생성_테스트() {
        // given
        Book book = Book.builder()
            .title("스프링 부트와 AWS")
            .author("이동욱")
            .isbn("9788965402602")
            .price(new BigDecimal("27000"))
            .available(true)
            .createdDate(LocalDateTime.now())
            .build();
        
        // when & then
        assertThat(book.getTitle()).isEqualTo("스프링 부트와 AWS");
        assertThat(book.getIsbn()).isEqualTo("9788965402602");
    }
    
    @Test
    void ISBN_검증_테스트() {
        // given
        String validIsbn = "9788965402602";
        String invalidIsbn = "1234567890123";
        
        // when & then
        assertThat(Book.isValidISBN(validIsbn)).isTrue();
        assertThat(Book.isValidISBN(invalidIsbn)).isFalse();
    }
    
    @Test
    void Soft_Delete_테스트() {
        // given
        Book book = createTestBook();
        
        // when
        book.markAsDeleted();
        
        // then
        assertThat(book.isDeleted()).isTrue();
        assertThat(book.getDeletedAt()).isNotNull();
    }
}
```

## ✅ 완료 조건
- [ ] Builder 패턴 테스트
- [ ] ISBN 검증 로직 테스트
- [ ] Soft Delete 기능 테스트
- [ ] 필드 유효성 검증 테스트

---
**Parent Task**: #27 | **Sub-task**: Book Entity 단위 테스트 작성

---

## 🔒 #34 [SUB-TASK] Soft Delete 기능 구현

**📊 상태:** OPEN  
**👤 작성자:** goodjwon  
**📅 생성일:** 2025-08-30  
**🔄 수정일:** 2025-08-30  
**🏷️ 라벨:** `sub-task` `priority:high` `domain:book` `layer:entity`  
**🔗 링크:** https://github.com/goodjwon/day_by_spring/issues/34

### 📝 설명

# 🗑️ Soft Delete 기능 구현

## 📋 Sub-task 정보
- **Task**: [TASK] Book Entity 설계 및 구현 (#27)
- **상태**: ⏳ 대기중
- **우선순위**: 🟡 High
- **예상 소요시간**: 1시간

## 📝 Sub-task 설명
도서 삭제 시 물리적 삭제 대신 논리적 삭제(Soft Delete)를 구현합니다.

## 🎯 구현 세부사항
```java
@Entity
@SQLDelete(sql = "UPDATE book SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Book {
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    // Soft Delete 상태 확인 메서드
    public boolean isDeleted() {
        return deletedAt != null;
    }
    
    // Soft Delete 실행 메서드
    public void markAsDeleted() {
        this.deletedAt = LocalDateTime.now();
    }
    
    // Soft Delete 복구 메서드
    public void restore() {
        this.deletedAt = null;
    }
}
```

## ✅ 완료 조건
- [ ] deletedAt 필드 추가
- [ ] @SQLDelete 어노테이션 적용
- [ ] @Where 어노테이션 적용
- [ ] Soft Delete 관련 메서드 구현
- [ ] Soft Delete 테스트

---
**Parent Task**: #27 | **Sub-task**: Soft Delete 기능 구현

---

## 🔒 #33 [SUB-TASK] ISBN 검증 로직 구현

**📊 상태:** OPEN  
**👤 작성자:** goodjwon  
**📅 생성일:** 2025-08-30  
**🔄 수정일:** 2025-08-30  
**🏷️ 라벨:** `sub-task` `priority:high` `domain:book` `type:validation`  
**🔗 링크:** https://github.com/goodjwon/day_by_spring/issues/33

### 📝 설명

# 🔍 ISBN 검증 로직 구현

## 📋 Sub-task 정보
- **Task**: [TASK] Book Entity 설계 및 구현 (#27)
- **상태**: ⏳ 대기중
- **우선순위**: 🟡 High
- **예상 소요시간**: 1.5시간

## 📝 Sub-task 설명
ISBN 형식 검증과 체크썸 검증 로직을 구현합니다.

## 🎯 구현 세부사항
```java
@Entity
public class Book {
    
    @Column(nullable = false, unique = true, length = 13)
    @Pattern(regexp = "^(978|979)\\d{10}$", 
             message = "ISBN은 978 또는 979로 시작하는 13자리 숫자여야 합니다")
    private String isbn;
    
    // ISBN 체크썸 검증 메서드
    public static boolean isValidISBN(String isbn) {
        if (isbn == null || isbn.length() != 13) {
            return false;
        }
        
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = Character.getNumericValue(isbn.charAt(i));
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        
        int checkDigit = (10 - (sum % 10)) % 10;
        return checkDigit == Character.getNumericValue(isbn.charAt(12));
    }
}
```

## ✅ 완료 조건
- [ ] ISBN 형식 정규식 패턴 적용
- [ ] ISBN 체크썸 검증 메서드 구현
- [ ] 유효성 검증 어노테이션 적용
- [ ] 검증 로직 단위 테스트

---
**Parent Task**: #27 | **Sub-task**: ISBN 검증 로직 구현

---

## 🔒 #32 [SUB-TASK] Book 기본 필드 및 어노테이션 구현

**📊 상태:** OPEN  
**👤 작성자:** goodjwon  
**📅 생성일:** 2025-08-30  
**🔄 수정일:** 2025-08-30  
**🏷️ 라벨:** `sub-task` `priority:critical` `domain:book` `layer:entity`  
**🔗 링크:** https://github.com/goodjwon/day_by_spring/issues/32

### 📝 설명

# 📖 Book 기본 필드 및 어노테이션 구현

## 📋 Sub-task 정보
- **Task**: [TASK] Book Entity 설계 및 구현 (#27)
- **상태**: ⏳ 대기중
- **우선순위**: 🔥 Critical
- **예상 소요시간**: 2시간

## 📝 Sub-task 설명
Book 엔티티의 기본 필드를 정의하고 JPA 어노테이션을 적용합니다.

## 🎯 구현 세부사항
```java
@Entity
@Table(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(nullable = false, length = 100)
    private String author;
    
    @Column(nullable = false, unique = true, length = 13)
    private String isbn;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(nullable = false)
    private Boolean available = true;
    
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;
}
```

## ✅ 완료 조건
- [ ] 기본 필드 정의 (id, title, author, isbn, price, available, createdDate)
- [ ] JPA 어노테이션 적용
- [ ] 테이블명 및 컬럼명 설정
- [ ] 제약조건 설정 (nullable, unique, precision)

---
**Parent Task**: #27 | **Sub-task**: Book 기본 필드 및 어노테이션 구현

---

## 🔒 #31 [TASK] 도서 검증 및 예외 처리

**📊 상태:** OPEN  
**👤 작성자:** goodjwon  
**📅 생성일:** 2025-08-30  
**🔄 수정일:** 2025-08-30  
**🏷️ 라벨:** `task` `priority:high` `domain:book` `type:validation`  
**🔗 링크:** https://github.com/goodjwon/day_by_spring/issues/31

### 📝 설명

# 🛡️ 도서 검증 및 예외 처리

## 📋 Task 정보
- **Story**: [STORY] 도서 기본 CRUD 구현 (#23)
- **상태**: ⏳ 대기중
- **우선순위**: 🟡 High
- **예상 소요시간**: 4시간

## 📝 Task 설명
도서 데이터 입력 검증과 비즈니스 예외 처리를 구현합니다.
ISBN 형식 검증, 중복 처리, 에러 응답 표준화를 포함합니다.

## 🎯 구현 요구사항
- [ ] Bean Validation 적용 (@NotBlank, @DecimalMin)
- [ ] ISBN 형식 검증 (정규식)
- [ ] 커스텀 Validator 구현 (ISBN 중복)
- [ ] BookException 커스텀 예외 클래스
- [ ] GlobalExceptionHandler 확장
- [ ] 에러 응답 DTO 설계

## 📋 Sub-task Issues
- [ ] Bean Validation 어노테이션 적용
- [ ] ISBN 형식 검증 구현
- [ ] 커스텀 예외 클래스 생성
- [ ] 예외 처리기 확장

## 🔧 기술 스택
- Bean Validation
- 정규식 (ISBN 검증)
- @ControllerAdvice
- 커스텀 Exception

## ✅ Definition of Done
- [ ] 모든 입력 검증 구현
- [ ] ISBN 형식 검증 완료
- [ ] 예외 처리 로직 완료
- [ ] 에러 케이스 테스트
- [ ] 문서화 완료

---
**Parent Story**: #23 | **Task**: 도서 검증 및 예외 처리

---

## 🔒 #30 [TASK] BookController REST API 구현

**📊 상태:** OPEN  
**👤 작성자:** goodjwon  
**📅 생성일:** 2025-08-30  
**🔄 수정일:** 2025-08-30  
**🏷️ 라벨:** `task` `priority:critical` `domain:book` `layer:controller`  
**🔗 링크:** https://github.com/goodjwon/day_by_spring/issues/30

### 📝 설명

# 🌐 BookController REST API 구현

## 📋 Task 정보
- **Story**: [STORY] 도서 기본 CRUD 구현 (#23)
- **상태**: ⏳ 대기중
- **우선순위**: 🔥 Critical
- **예상 소요시간**: 5시간

## 📝 Task 설명
RESTful 원칙을 따르는 도서 관리 API를 구현합니다.
적절한 HTTP 상태 코드와 에러 처리를 포함합니다.

## 🎯 구현 요구사항
- [ ] BookController 클래스 생성
- [ ] POST /api/books (도서 등록)
- [ ] GET /api/books/{id} (도서 단건 조회)
- [ ] GET /api/books (도서 목록 조회)
- [ ] PUT /api/books/{id} (도서 정보 수정)
- [ ] DELETE /api/books/{id} (도서 삭제 - Soft Delete)
- [ ] 적절한 HTTP 상태 코드 반환

## 📋 Sub-task Issues
- [ ] Controller 기본 구조 생성
- [ ] CRUD API 엔드포인트 구현
- [ ] DTO 클래스 설계 및 구현
- [ ] HTTP 상태 코드 처리
- [ ] API 통합 테스트

## 🔧 기술 스택
- Spring Web MVC
- @RestController
- ResponseEntity
- DTO Pattern
- Bean Validation

## ✅ Definition of Done
- [ ] 모든 REST API 구현
- [ ] DTO 변환 로직 완료
- [ ] HTTP 상태 코드 적절히 반환
- [ ] API 통합 테스트 완료
- [ ] API 문서화 완료

---
**Parent Story**: #23 | **Task**: BookController REST API 구현

---

## 🔒 #29 [TASK] BookService 비즈니스 로직 구현

**📊 상태:** OPEN  
**👤 작성자:** goodjwon  
**📅 생성일:** 2025-08-30  
**🔄 수정일:** 2025-08-30  
**🏷️ 라벨:** `task` `priority:critical` `domain:book` `layer:service`  
**🔗 링크:** https://github.com/goodjwon/day_by_spring/issues/29

### 📝 설명

# ⚙️ BookService 비즈니스 로직 구현

## 📋 Task 정보
- **Story**: [STORY] 도서 기본 CRUD 구현 (#23)
- **상태**: ⏳ 대기중
- **우선순위**: 🔥 Critical
- **예상 소요시간**: 6시간

## 📝 Task 설명
도서 관리 비즈니스 로직을 구현합니다.
ISBN 중복 검증, Soft Delete 처리, 트랜잭션 관리를 포함합니다.

## 🎯 구현 요구사항
- [ ] BookService 인터페이스 정의
- [ ] BookServiceImpl 구현 클래스 생성
- [ ] 도서 등록 로직 (ISBN 중복 검증)
- [ ] 도서 조회 로직 (Soft Delete 고려)
- [ ] 도서 수정 로직
- [ ] 도서 삭제 로직 (Soft Delete)
- [ ] 비즈니스 검증 로직

## 📋 Sub-task Issues
- [ ] Service 인터페이스 설계
- [ ] CRUD 비즈니스 로직 구현
- [ ] ISBN 중복 검증 로직
- [ ] Soft Delete 처리 로직
- [ ] Service 계층 테스트

## 🔧 기술 스택
- Spring Framework
- @Transactional
- 커스텀 Exception
- Soft Delete Pattern

## ✅ Definition of Done
- [ ] 모든 비즈니스 로직 구현
- [ ] ISBN 중복 검증 완료
- [ ] Soft Delete 처리 완료
- [ ] 트랜잭션 테스트 완료
- [ ] 예외 시나리오 테스트

---
**Parent Story**: #23 | **Task**: BookService 비즈니스 로직 구현

---

## 🔒 #28 [TASK] BookRepository 인터페이스 구현

**📊 상태:** OPEN  
**👤 작성자:** goodjwon  
**📅 생성일:** 2025-08-30  
**🔄 수정일:** 2025-08-30  
**🏷️ 라벨:** `task` `priority:critical` `domain:book` `layer:repository`  
**🔗 링크:** https://github.com/goodjwon/day_by_spring/issues/28

### 📝 설명

# 📚 BookRepository 인터페이스 구현

## 📋 Task 정보
- **Story**: [STORY] 도서 기본 CRUD 구현 (#23)
- **상태**: ⏳ 대기중
- **우선순위**: 🔥 Critical
- **예상 소요시간**: 4시간

## 📝 Task 설명
도서 데이터 접근을 위한 Repository 인터페이스를 구현합니다.
ISBN 기반 조회, 제목/저자 검색, Soft Delete 처리를 포함합니다.

## 🎯 구현 요구사항
- [ ] BookRepository 인터페이스 생성
- [ ] JpaRepository 상속
- [ ] ISBN으로 도서 찾기 메서드
- [ ] 제목 부분 검색 메서드
- [ ] 저자 부분 검색 메서드
- [ ] 삭제되지 않은 도서만 조회
- [ ] 가격 범위 검색 메서드

## 📋 Sub-task Issues
- [ ] 기본 Repository 인터페이스 생성
- [ ] Custom Query Methods 구현
- [ ] Soft Delete 쿼리 구현
- [ ] Repository 테스트 작성

## 🔧 기술 스택
- Spring Data JPA
- Query Methods
- @Query 어노테이션
- Soft Delete

## ✅ Definition of Done
- [ ] Repository 인터페이스 구현
- [ ] 모든 Query Methods 구현
- [ ] Soft Delete 쿼리 완료
- [ ] Repository 테스트 작성
- [ ] 성능 검증 완료

---
**Parent Story**: #23 | **Task**: BookRepository 인터페이스 구현

---

## 🔒 #27 [TASK] Book Entity 설계 및 구현

**📊 상태:** OPEN  
**👤 작성자:** goodjwon  
**📅 생성일:** 2025-08-30  
**🔄 수정일:** 2025-08-30  
**🏷️ 라벨:** `task` `priority:critical` `domain:book` `layer:entity`  
**🔗 링크:** https://github.com/goodjwon/day_by_spring/issues/27

### 📝 설명

# 📖 Book Entity 설계 및 구현

## 📋 Task 정보
- **Story**: [STORY] 도서 기본 CRUD 구현 (#23)
- **상태**: ⏳ 대기중
- **우선순위**: 🔥 Critical
- **예상 소요시간**: 5시간

## 📝 Task 설명
도서 정보를 저장할 JPA Entity를 설계하고 구현합니다.
ISBN, 제목, 저자, 가격, 재고 수량 등의 필드와 적절한 제약조건을 포함합니다.

## 🎯 구현 요구사항
- [ ] Book 엔티티 클래스 생성
- [ ] 필드 정의 (id, title, author, isbn, price, available, createdDate)
- [ ] JPA 어노테이션 적용
- [ ] ISBN 유니크 제약조건 설정
- [ ] Soft Delete 필드 추가 (deletedAt)
- [ ] Lombok 어노테이션 적용
- [ ] Audit 필드 추가

## 📋 Sub-task Issues
- [ ] 기본 필드 및 어노테이션 구현
- [ ] ISBN 검증 로직 구현
- [ ] Soft Delete 기능 구현
- [ ] 단위 테스트 작성

## 🔧 기술 스택
- Spring Data JPA
- Lombok
- Bean Validation
- Soft Delete Pattern

## ✅ Definition of Done
- [ ] Book 엔티티 구현 완료
- [ ] ISBN 유니크 제약조건 설정
- [ ] Soft Delete 기능 구현
- [ ] 단위 테스트 작성
- [ ] 코드 리뷰 완료

---
**Parent Story**: #23 | **Task**: Book Entity 설계 및 구현

---

## 🔒 #26 [STORY] 도서 카테고리 관리

**📊 상태:** OPEN  
**👤 작성자:** goodjwon  
**📅 생성일:** 2025-08-30  
**🔄 수정일:** 2025-08-30  
**🏷️ 라벨:** `story` `priority:medium` `domain:book`  
**🔗 링크:** https://github.com/goodjwon/day_by_spring/issues/26

### 📝 설명

# 🏷️ 도서 카테고리 관리

## 📋 Story 정보
- **Epic**: [EPIC] 도서 관리 시스템 구현 (#5)
- **상태**: ⏳ 대기중 (0% 완료)
- **우선순위**: 🔵 Medium
- **담당자**: TBD

## 📝 Story 설명
도서 분류를 위한 카테고리 시스템을 구현합니다.
계층형 카테고리 구조와 카테고리별 도서 관리 기능을 제공합니다.

## 🎯 인수 조건 (Acceptance Criteria)
- [ ] 카테고리 생성 API (`POST /api/categories`)
- [ ] 카테고리 조회 API (`GET /api/categories`)
- [ ] 카테고리 수정 API (`PUT /api/categories/{id}`)
- [ ] 카테고리 삭제 API (`DELETE /api/categories/{id}`)
- [ ] 계층형 카테고리 구조 지원
- [ ] 카테고리별 도서 목록 조회
- [ ] 카테고리 통계 기능

## 📋 관련 Task Issues
- [ ] Category Entity 설계
- [ ] 계층형 구조 구현
- [ ] 카테고리 관리 서비스
- [ ] 도서-카테고리 연관관계
- [ ] 카테고리 통계 기능

## ✅ Definition of Done
- [ ] 계층형 카테고리 구현
- [ ] 카테고리 CRUD 완료
- [ ] 도서 연관 기능 구현
- [ ] 통계 기능 완료
- [ ] API 문서화 완료

---
**Parent Epic**: #5 | **Story**: 도서 카테고리 관리

---

## 🔒 #25 [STORY] 도서 재고 관리

**📊 상태:** OPEN  
**👤 작성자:** goodjwon  
**📅 생성일:** 2025-08-30  
**🔄 수정일:** 2025-08-30  
**🏷️ 라벨:** `story` `priority:high` `domain:book`  
**🔗 링크:** https://github.com/goodjwon/day_by_spring/issues/25

### 📝 설명

# 📦 도서 재고 관리

## 📋 Story 정보
- **Epic**: [EPIC] 도서 관리 시스템 구현 (#5)
- **상태**: ⏳ 대기중 (0% 완료)
- **우선순위**: 🟡 High
- **담당자**: TBD

## 📝 Story 설명
도서의 재고 상태를 관리하고, 입고/출고 처리 및 재고 부족 알림 기능을 구현합니다.
대여 시스템과 연동하여 실시간 재고 관리를 제공합니다.

## 🎯 인수 조건 (Acceptance Criteria)
- [ ] 도서 재고 조회 API (`GET /api/books/{id}/stock`)
- [ ] 도서 입고 처리 API (`POST /api/books/{id}/stock/increase`)
- [ ] 도서 출고 처리 API (`POST /api/books/{id}/stock/decrease`)
- [ ] 재고 이력 관리 (`GET /api/books/{id}/stock/history`)
- [ ] 재고 부족 알림 시스템
- [ ] 최소 재고 설정 기능
- [ ] 재고 현황 대시보드

## 📋 관련 Task Issues
- [ ] Stock Entity 설계
- [ ] 재고 관리 서비스 구현
- [ ] 재고 이력 추적 시스템
- [ ] 알림 시스템 연동
- [ ] 재고 통계 기능

## ✅ Definition of Done
- [ ] 재고 CRUD 기능 완료
- [ ] 실시간 재고 업데이트
- [ ] 재고 이력 추적 완료
- [ ] 알림 시스템 연동
- [ ] 통계 대시보드 구현

---
**Parent Epic**: #5 | **Story**: 도서 재고 관리

---

## 🔒 #24 [STORY] 도서 검색 및 필터링

**📊 상태:** OPEN  
**👤 작성자:** goodjwon  
**📅 생성일:** 2025-08-30  
**🔄 수정일:** 2025-08-30  
**🏷️ 라벨:** `story` `priority:high` `domain:book`  
**🔗 링크:** https://github.com/goodjwon/day_by_spring/issues/24

### 📝 설명

# 🔍 도서 검색 및 필터링

## 📋 Story 정보
- **Epic**: [EPIC] 도서 관리 시스템 구현 (#5)
- **상태**: ⏳ 대기중 (0% 완료)
- **우선순위**: 🟡 High
- **담당자**: TBD

## 📝 Story 설명
도서 검색 기능을 구현하여 제목, 저자, ISBN, 카테고리 등의 조건으로 도서를 검색할 수 있도록 합니다.
고급 검색 옵션과 페이징, 정렬 기능을 제공합니다.

## 🎯 인수 조건 (Acceptance Criteria)
- [ ] 제목으로 도서 검색 (`GET /api/books?title=스프링`)
- [ ] 저자로 도서 검색 (`GET /api/books?author=김영한`)
- [ ] ISBN으로 도서 검색 (`GET /api/books?isbn=978-1234567890`)
- [ ] 카테고리별 필터링 (`GET /api/books?category=IT`)
- [ ] 복합 조건 검색 지원
- [ ] 가격 범위 검색 (`GET /api/books?minPrice=10000&maxPrice=50000`)
- [ ] 출간일 범위 검색
- [ ] 페이징 및 정렬 기능

## 📋 관련 Task Issues
- [ ] 검색 조건 DTO 설계
- [ ] JPA Specification 구현
- [ ] 전체 텍스트 검색 구현
- [ ] 검색 성능 최적화
- [ ] 검색 결과 캐싱

## ✅ Definition of Done
- [ ] 모든 검색 조건 구현
- [ ] 성능 최적화 완료
- [ ] 검색 결과 정확도 검증
- [ ] API 문서화 완료

---
**Parent Epic**: #5 | **Story**: 도서 검색 및 필터링

---

## 🔒 #23 [STORY] 도서 기본 CRUD 구현

**📊 상태:** OPEN  
**👤 작성자:** goodjwon  
**📅 생성일:** 2025-08-30  
**🔄 수정일:** 2025-08-30  
**🏷️ 라벨:** `story` `priority:critical` `domain:book`  
**🔗 링크:** https://github.com/goodjwon/day_by_spring/issues/23

### 📝 설명

# 📚 도서 기본 CRUD 구현

## 📋 Story 정보
- **Epic**: [EPIC] 도서 관리 시스템 구현 (#5)
- **상태**: 🔄 진행중 (60% 완료)
- **우선순위**: 🔥 Critical
- **담당자**: TBD

## 📝 Story 설명
도서의 기본적인 생성, 조회, 수정, 삭제 기능을 구현합니다.
ISBN 기반 도서 등록과 재고 관리 기능을 포함합니다.

## 🎯 인수 조건 (Acceptance Criteria)
- [ ] 도서 등록 API (`POST /api/books`)
- [ ] 도서 단건 조회 API (`GET /api/books/{id}`)
- [ ] 도서 목록 조회 API (`GET /api/books`)
- [ ] 도서 정보 수정 API (`PUT /api/books/{id}`)
- [ ] 도서 삭제 API (`DELETE /api/books/{id}`) - Soft Delete
- [ ] ISBN 중복 검증

## 📋 관련 Task Issues
- [ ] Book Entity 설계 및 구현
- [ ] BookRepository 인터페이스 구현
- [ ] BookService 비즈니스 로직 구현
- [ ] BookController REST API 구현
- [ ] 도서 검증 및 예외 처리

## ✅ Definition of Done
- [ ] 모든 CRUD API 구현 완료
- [ ] ISBN 검증 로직 구현
- [ ] Soft Delete 처리 완료
- [ ] Unit/Integration 테스트 작성
- [ ] API 문서화 완료

---
**Parent Epic**: #5 | **Story**: 도서 기본 CRUD 구현

---

## 🔒 #5 [EPIC] 도서 관리 시스템 구현

**📊 상태:** OPEN  
**👤 작성자:** goodjwon  
**📅 생성일:** 2025-08-30  
**🔄 수정일:** 2025-08-30  
**🏷️ 라벨:** `epic` `priority:high` `domain:book`  
**🔗 링크:** https://github.com/goodjwon/day_by_spring/issues/5

### 📝 설명

# 📚 도서 관리 시스템 구현

## 📊 Epic 정보
- **상태**: 🔄 진행중 (60% 완료)
- **기간**: 2025-07-15 ~ 2025-08-10
- **우선순위**: 🟡 High

## 🔧 포함 기능
- ✅ 도서 등록 API
- ✅ 도서 단건 조회 API
- ✅ 도서 목록 조회 API
- ⏳ 도서 검색 기능 (제목, 저자, ISBN)
- ⏳ 도서 정보 수정 API
- ⏳ 도서 삭제 API (soft delete)
- ⏳ 도서 재고 관리

## 🎓 학습 목표
- JPA Entity 설계 및 관계 매핑
- Query Method 활용
- 검색 기능 구현 (Like, Contains)
- Soft Delete 패턴 구현

## 📋 관련 Story Issues
- [ ] 도서 기본 CRUD 구현
- [ ] 도서 검색 및 필터링
- [ ] 도서 재고 관리
- [ ] 도서 카테고리 관리

## ✅ Definition of Done
- [ ] 모든 도서 관리 API 구현
- [ ] 도서 검색 기능 완료
- [ ] 재고 관리 로직 구현
- [ ] 테스트 케이스 작성
- [ ] API 문서화

---
**Epic**: 도서 관리 시스템 구현
**Created**: $(date +%Y-%m-%d)

---

