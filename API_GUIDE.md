
# Spring Library System API 가이드

## 📚 개요

이 문서는 Spring Library System의 REST API 사용법을 설명합니다. 실제 호출 가능한 순서로 예제를 제공하여 API를 쉽게 테스트할 수 있습니다.

## 🚀 시작하기

### 서버 실행
```bash
mvn spring-boot:run
```

### Swagger UI 접근
- **URL**: http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## 📋 API 테스트 순서

### 1단계: 회원 관리 (Members API)

#### 1.1 회원 가입
새로운 회원을 등록합니다.

```bash
curl -X POST "http://localhost:8080/api/members" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "홍길동",
    "email": "hong@example.com",
    "membershipType": "REGULAR"
  }'
```

**응답 예제:**
```json
{
  "id": 1,
  "name": "홍길동",
  "email": "hong@example.com",
  "membershipType": "REGULAR",
  "joinDate": "2025-01-15T10:30:00"
}
```

#### 1.2 회원 목록 조회 (페이징)
등록된 회원들을 페이징으로 조회합니다.

```bash
curl -X GET "http://localhost:8080/api/members?page=0&size=10"
```

#### 1.3 회원 상세 조회
특정 회원의 상세 정보를 조회합니다.

```bash
curl -X GET "http://localhost:8080/api/members/1"
```

#### 1.4 추가 회원 생성 (테스트용)
```bash
# 프리미엄 회원
curl -X POST "http://localhost:8080/api/members" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "김개발",
    "email": "kim.dev@example.com",
    "membershipType": "PREMIUM"
  }'

# 일반 회원  
curl -X POST "http://localhost:8080/api/members" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "박스프링",
    "email": "park.spring@example.com",
    "membershipType": "REGULAR"
  }'
```

### 2단계: 도서 관리 (Books API)

#### 2.1 도서 등록
새로운 도서를 등록합니다.

```bash
curl -X POST "http://localhost:8080/api/books" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Clean Code",
    "author": "Robert C. Martin",
    "isbn": "9780132350884",
    "price": 45000,
    "available": true
  }'
```

**응답 예제:**
```json
{
  "id": 1,
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "isbn": "9780132350884",
  "price": 45000,
  "available": true,
  "createdDate": "2025-01-15T10:35:00"
}
```

#### 2.2 여러 도서 등록 (테스트용)
```bash
# Spring in Action
curl -X POST "http://localhost:8080/api/books" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Spring in Action",
    "author": "Craig Walls",
    "isbn": "9781617294945",
    "price": 52000,
    "available": true
  }'

# Effective Java
curl -X POST "http://localhost:8080/api/books" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Effective Java",
    "author": "Joshua Bloch",
    "isbn": "9780134685991",
    "price": 48000,
    "available": true
  }'

# Design Patterns  
curl -X POST "http://localhost:8080/api/books" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Design Patterns",
    "author": "Gang of Four",
    "isbn": "9780201633612",
    "price": 55000,
    "available": false
  }'
```

#### 2.3 도서 목록 조회 (페이징)
등록된 도서들을 페이징으로 조회합니다.

```bash
curl -X GET "http://localhost:8080/api/books?page=0&size=5&sort=createdDate&direction=desc"
```

#### 2.4 도서 상세 조회
특정 도서의 상세 정보를 조회합니다.

```bash
curl -X GET "http://localhost:8080/api/books/1"
```

#### 2.5 ISBN으로 도서 조회
ISBN을 사용해서 도서를 조회합니다.

```bash
curl -X GET "http://localhost:8080/api/books/isbn/9780132350884"
```

### 3단계: 도서 검색 기능

#### 3.1 제목으로 검색
```bash
curl -X GET "http://localhost:8080/api/books/search/title?title=Clean"
```

#### 3.2 저자로 검색
```bash
curl -X GET "http://localhost:8080/api/books/search/author?author=Martin"
```

#### 3.3 키워드 검색 (제목 + 저자)
```bash
curl -X GET "http://localhost:8080/api/books/search/keyword?keyword=Java"
```

#### 3.4 가격 범위로 검색
```bash
curl -X GET "http://localhost:8080/api/books/search/price?minPrice=40000&maxPrice=50000"
```

#### 3.5 복합 조건 검색 (페이징)
여러 조건을 조합하여 검색합니다.

```bash
curl -X GET "http://localhost:8080/api/books/search?title=Clean&author=Martin&minPrice=40000&maxPrice=60000&available=true&page=0&size=10"
```

### 4단계: 도서 관리 기능

#### 4.1 도서 정보 수정
```bash
curl -X PUT "http://localhost:8080/api/books/1" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Clean Code - Updated Edition",
    "author": "Robert C. Martin",
    "isbn": "9780132350884",
    "price": 47000,
    "available": true
  }'
```

#### 4.2 재고 상태 업데이트
```bash
# 재고 없음으로 변경
curl -X PATCH "http://localhost:8080/api/books/1/availability?available=false"

# 재고 있음으로 변경
curl -X PATCH "http://localhost:8080/api/books/1/availability?available=true"
```

#### 4.3 재고 상태별 도서 조회
```bash
# 재고 있는 도서만 조회
curl -X GET "http://localhost:8080/api/books/availability/true"

# 재고 없는 도서만 조회
curl -X GET "http://localhost:8080/api/books/availability/false"
```

### 5단계: 관리 기능

#### 5.1 ISBN 중복 확인
새 도서 등록 전에 ISBN 중복을 확인합니다.

```bash
curl -X GET "http://localhost:8080/api/books/validate/isbn?isbn=9780132350884"
```

#### 5.2 도서 통계 조회
전체 도서, 활성 도서, 삭제된 도서 수를 조회합니다.

```bash
curl -X GET "http://localhost:8080/api/books/statistics"
```

**응답 예제:**
```json
{
  "totalBooks": 10,
  "activeBooks": 8,
  "deletedBooks": 2
}
```

### 6단계: Soft Delete 기능

#### 6.1 도서 삭제 (Soft Delete)
도서를 논리적으로 삭제합니다.

```bash
curl -X DELETE "http://localhost:8080/api/books/4"
```

#### 6.2 삭제된 도서 복원
```bash
curl -X PATCH "http://localhost:8080/api/books/4/restore"
```

### 7단계: 회원 고급 기능

#### 7.1 회원 이름으로 검색
```bash
curl -X GET "http://localhost:8080/api/members/search?name=홍"
```

#### 7.2 회원십 업그레이드
```bash
curl -X PUT "http://localhost:8080/api/members/1/membership?membershipType=PREMIUM"
```

#### 7.3 회원 대출 한도 조회
```bash
curl -X GET "http://localhost:8080/api/members/1/loan-limit"
```

#### 7.4 이메일 중복 확인
```bash
curl -X GET "http://localhost:8080/api/members/email/validate?email=test@example.com"
```

### 8단계: 주문 관리 (Orders API)

#### 8.1 주문 생성
새로운 주문을 생성합니다.

```bash
curl -X POST "http://localhost:8080/api/orders" \
  -H "Content-Type: application/json" \
  -d '{
    "bookIds": [1, 2],
    "customerEmail": "hong@example.com"
  }'
```

**응답 예제:**
```json
{
  "id": 1,
  "totalAmount": 97000,
  "orderDate": "2025-10-03T22:00:00",
  "status": "PENDING",
  "items": [
    {
      "id": 1,
      "bookId": 1,
      "bookTitle": "Clean Code",
      "bookAuthor": "Robert C. Martin",
      "quantity": 1,
      "price": 45000
    },
    {
      "id": 2,
      "bookId": 2,
      "bookTitle": "Spring in Action",
      "bookAuthor": "Craig Walls",
      "quantity": 1,
      "price": 52000
    }
  ]
}
```

#### 8.2 주문 단건 조회
특정 주문의 상세 정보를 조회합니다.

```bash
curl -X GET "http://localhost:8080/api/orders/1"
```

#### 8.3 주문 목록 조회 (페이징)
등록된 주문들을 페이징으로 조회합니다.

```bash
curl -X GET "http://localhost:8080/api/orders?page=0&size=10&sort=orderDate&direction=desc"
```

#### 8.4 주문 상태 변경
주문의 상태를 변경합니다.

```bash
# PENDING -> CONFIRMED
curl -X PATCH "http://localhost:8080/api/orders/1/status?status=CONFIRMED"

# CONFIRMED -> SHIPPED
curl -X PATCH "http://localhost:8080/api/orders/1/status?status=SHIPPED"

# SHIPPED -> DELIVERED
curl -X PATCH "http://localhost:8080/api/orders/1/status?status=DELIVERED"
```

**주문 상태:**
- `PENDING`: 주문 대기
- `CONFIRMED`: 주문 확인
- `SHIPPED`: 배송 중
- `DELIVERED`: 배송 완료
- `CANCELLED`: 주문 취소

#### 8.5 주문 수정
PENDING 상태의 주문만 수정할 수 있습니다.

```bash
curl -X PUT "http://localhost:8080/api/orders/1" \
  -H "Content-Type: application/json" \
  -d '{
    "bookIds": [1, 2, 3],
    "customerEmail": "hong@example.com"
  }'
```

#### 8.6 주문 취소
주문을 취소합니다. (배송 완료된 주문은 취소 불가)

```bash
curl -X POST "http://localhost:8080/api/orders/1/cancel"
```

#### 8.7 상태별 주문 조회
특정 상태의 주문들을 조회합니다.

```bash
# PENDING 상태 주문 조회
curl -X GET "http://localhost:8080/api/orders/status/PENDING?page=0&size=10"

# CONFIRMED 상태 주문 조회
curl -X GET "http://localhost:8080/api/orders/status/CONFIRMED?page=0&size=10"
```

#### 8.8 기간별 주문 조회
특정 기간의 주문들을 조회합니다.

```bash
curl -X GET "http://localhost:8080/api/orders/period?startDate=2025-01-01T00:00:00&endDate=2025-12-31T23:59:59&page=0&size=10"
```

#### 8.9 주문 통계 조회
전체 주문에 대한 통계를 조회합니다.

```bash
curl -X GET "http://localhost:8080/api/orders/statistics"
```

**응답 예제:**
```json
{
  "totalOrders": 100,
  "pendingOrders": 20,
  "confirmedOrders": 30,
  "shippedOrders": 25,
  "deliveredOrders": 20,
  "cancelledOrders": 5,
  "totalRevenue": 5000000,
  "averageOrderAmount": 50000
}
```

#### 8.10 일별 주문 통계
특정 기간의 일별 주문 통계를 조회합니다.

```bash
curl -X GET "http://localhost:8080/api/orders/statistics/daily?startDate=2025-10-01T00:00:00&endDate=2025-10-31T23:59:59"
```

**응답 예제:**
```json
[
  {
    "date": "2025-10-01T00:00:00",
    "orderCount": 15,
    "totalAmount": 750000
  },
  {
    "date": "2025-10-02T00:00:00",
    "orderCount": 12,
    "totalAmount": 600000
  }
]
```

#### 8.11 상위 판매 도서 조회
가장 많이 판매된 도서 목록을 조회합니다.

```bash
curl -X GET "http://localhost:8080/api/orders/statistics/top-books?limit=10"
```

**응답 예제:**
```json
[
  {
    "bookId": 1,
    "bookTitle": "Clean Code",
    "bookAuthor": "Robert C. Martin",
    "totalQuantity": 50,
    "totalRevenue": 2250000
  },
  {
    "bookId": 2,
    "bookTitle": "Spring in Action",
    "bookAuthor": "Craig Walls",
    "totalQuantity": 45,
    "totalRevenue": 2340000
  }
]
```

## 📊 완전한 테스트 시나리오

다음은 모든 기능을 순서대로 테스트하는 완전한 스크립트입니다:

```bash
#!/bin/bash

BASE_URL="http://localhost:8080"

echo "=== 1. 회원 가입 ==="
curl -X POST "$BASE_URL/api/members" \
  -H "Content-Type: application/json" \
  -d '{"name": "홍길동", "email": "hong@example.com", "membershipType": "REGULAR"}'

echo -e "\n=== 2. 도서 등록 ==="
curl -X POST "$BASE_URL/api/books" \
  -H "Content-Type: application/json" \
  -d '{"title": "Clean Code", "author": "Robert C. Martin", "isbn": "9780132350884", "price": 45000, "available": true}'

echo -e "\n=== 3. 도서 목록 조회 ==="
curl -X GET "$BASE_URL/api/books?page=0&size=10"

echo -e "\n=== 4. 도서 검색 ==="
curl -X GET "$BASE_URL/api/books/search/title?title=Clean"

echo -e "\n=== 5. 도서 통계 ==="
curl -X GET "$BASE_URL/api/books/statistics"

echo -e "\n=== 6. 회원 목록 조회 ==="
curl -X GET "$BASE_URL/api/members?page=0&size=10"

echo -e "\n=== 7. 주문 생성 ==="
curl -X POST "$BASE_URL/api/orders" \
  -H "Content-Type: application/json" \
  -d '{"bookIds": [1, 2], "customerEmail": "hong@example.com"}'

echo -e "\n=== 8. 주문 목록 조회 ==="
curl -X GET "$BASE_URL/api/orders?page=0&size=10"

echo -e "\n=== 9. 주문 상태 변경 ==="
curl -X PATCH "$BASE_URL/api/orders/1/status?status=CONFIRMED"

echo -e "\n=== 10. 주문 통계 조회 ==="
curl -X GET "$BASE_URL/api/orders/statistics"

echo -e "\n=== 11. 상위 판매 도서 조회 ==="
curl -X GET "$BASE_URL/api/orders/statistics/top-books?limit=5"
```

## 🛠️ 개발자를 위한 정보

### 응답 코드
- **200 OK**: 성공적인 조회/수정
- **201 Created**: 성공적인 생성
- **204 No Content**: 성공적인 삭제
- **400 Bad Request**: 잘못된 요청 데이터
- **404 Not Found**: 리소스를 찾을 수 없음
- **409 Conflict**: 중복 데이터 (ISBN, Email)

### 페이징 파라미터
- **page**: 페이지 번호 (0부터 시작)
- **size**: 페이지 크기 (기본값: 10)
- **sort**: 정렬 기준 (기본값: createdDate)
- **direction**: 정렬 방향 (asc/desc, 기본값: desc)

### Validation 규칙

#### 도서 (Book)
- **title**: 필수, 최대 200자
- **author**: 필수, 최대 100자  
- **isbn**: 필수, ISBN 형식 (13자리 숫자 또는 하이픈 포함)
- **price**: 필수, 0보다 큰 수

#### 회원 (Member)
- **name**: 필수, 최대 100자
- **email**: 필수, 유효한 이메일 형식, 중복 불가
- **membershipType**: REGULAR, PREMIUM, SUSPENDED 중 하나

## 🔍 추가 기능

### H2 Console (개발 환경)
- **URL**: http://localhost:8080/h2-console
- **JDBC URL**: jdbc:h2:mem:devdb
- **Username**: sa
- **Password**: (빈 값)

### 로그 모니터링
애플리케이션 로그에서 AOP를 통한 실행 시간 로깅을 확인할 수 있습니다:
- `[CONTROLLER]` 태그: 컨트롤러 메서드 실행 로그
- `[SERVICE]` 태그: 서비스 메서드 실행 로그

## 📮 Postman으로 API 테스트하기

### 방법 1: Postman Collection Import
프로젝트 루트의 `postman-collection.json` 파일을 Postman에 import하세요:

1. **Postman 열기** → **Import** 버튼 클릭
2. **파일 업로드** → `postman-collection.json` 선택
3. **Import** 완료 후 "Spring Library System API" 컬렉션 확인

### 방법 2: OpenAPI 스펙에서 자동 생성
Swagger UI에서 직접 Postman Collection을 생성할 수 있습니다:

1. **서버 실행**: `mvn spring-boot:run`
2. **Swagger UI 접근**: http://localhost:8080/swagger-ui/index.html
3. **OpenAPI JSON 다운로드**: http://localhost:8080/v3/api-docs
4. **Postman Import** → **Link** → OpenAPI JSON URL 입력

### 컬렉션 특징
- **환경 변수 설정**: `baseUrl`, `memberId`, `bookId` 자동 설정
- **논리적 순서**: 실제 사용 시나리오에 따른 API 순서
- **완전한 요청**: Header, Body, Parameter 모두 사전 설정
- **테스트 데이터**: 실제 동작하는 샘플 데이터 포함

### 사용법
1. 먼저 **"1. Member Management"** 섹션에서 회원 생성
2. 다음 **"2. Book Management"** 섹션에서 도서 등록  
3. 이후 검색, 수정, 삭제 등 다른 기능 테스트

이 가이드를 통해 Spring Library System의 모든 API를 체계적으로 테스트할 수 있습니다! 🎉


Postman Collection 생성 완료

📁 생성된 파일들:

- postman-collection.json - Postman에 직접 import할 수 있는 컬렉션 파일
- 업데이트된 API_GUIDE.md - Postman 사용법 추가

📮 Postman에서 사용하는 방법:

1. 간단한 방법 (Collection Import):
1. Postman 열기
2. Import 버튼 클릭
3. postman-collection.json 파일 선택
4. Import 완료!

2. OpenAPI 방법:
1. 서버 실행: mvn spring-boot:run
2. Postman → Import → Link → http://localhost:8080/v3/api-docs 입력

✨ 컬렉션 특징:

- 27개 API 엔드포인트 모두 포함
- 환경 변수 설정 (baseUrl, memberId, bookId)
- 논리적 순서로 구성 (회원 생성 → 도서 등록 → 검색/관리)
- 실제 테스트 데이터 사전 설정
- 6개 폴더로 기능별 분류

🔄 사용 순서:

1. Member Management - 회원 생성
2. Book Management - 도서 등록
3. Book Search - 검색 기능
4. Book Management - 수정/재고 관리
5. Management Features - 통계/검증
6. Soft Delete Operations - 삭제/복원