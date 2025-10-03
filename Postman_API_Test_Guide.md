# BookController API Test Guide - Postman

이 가이드는 BookController의 모든 API 엔드포인트를 Postman으로 테스트하는 방법을 설명합니다.

## 파일 구성

- `BookController_API_Tests.postman_collection.json` - Postman 컬렉션 파일
- `BookController_API_Environment.postman_environment.json` - Postman 환경 변수 파일
- `Postman_API_Test_Guide.md` - 이 가이드 파일

## 설정 방법

### 1. Postman에서 컬렉션 Import
1. Postman 앱 실행
2. `Import` 버튼 클릭
3. `BookController_API_Tests.postman_collection.json` 파일 선택하여 Import

### 2. Environment 설정
1. Postman에서 `Environments` 탭 선택
2. `Import` 버튼 클릭
3. `BookController_API_Environment.postman_environment.json` 파일 선택하여 Import
4. Environment를 `BookController API Environment`로 설정

### 3. Spring Boot 애플리케이션 실행
```bash
# 프로젝트 루트에서 실행
mvn spring-boot:run
```

또는 IDE에서 `SpringBookstoreApplication.java` 실행

## API 테스트 카테고리

### 1. 도서 생성 (POST /api/books)
- **도서 생성 성공**: 정상적인 도서 데이터로 생성
- **제목 누락 시 400 에러**: 필수 필드 누락 테스트
- **잘못된 ISBN 형식**: 유효성 검증 테스트

### 2. 도서 조회 (GET /api/books/*)
- **도서 상세 조회**: ID로 특정 도서 조회
- **존재하지 않는 도서 조회**: 에러 처리 테스트
- **ISBN으로 도서 조회**: ISBN 기반 조회
- **활성 도서 목록 조회**: 페이징 기능 포함

### 3. 도서 수정 (PUT /api/books/{id})
- **도서 수정 성공**: 기존 도서 정보 업데이트

### 4. 도서 삭제 (DELETE /api/books/{id})
- **도서 삭제 성공**: Soft Delete 기능 테스트

### 5. 도서 검색 (GET /api/books/search/*)
- **제목으로 검색**: 부분 문자열 검색
- **저자로 검색**: 저자명 기반 검색
- **키워드 검색**: 제목 또는 저자에서 검색
- **가격 범위 검색**: 최소/최대 가격 필터
- **복합 조건 검색**: 여러 조건 조합 + 페이징

### 6. 재고 관리 (PATCH /api/books/*)
- **재고 상태 업데이트**: 도서 가용성 변경
- **재고 상태별 조회**: 가용/불가용 도서 필터링

### 7. 통계 및 유틸리티 (GET /api/books/*)
- **도서 통계 조회**: 전체/활성/삭제된 도서 수
- **ISBN 중복 확인**: 중복 검증 기능

### 8. 도서 복원 (PATCH /api/books/{id}/restore)
- **도서 복원 성공**: 삭제된 도서 복원

## 테스트 순서 권장 사항

1. **도서 생성** → 테스트용 도서 데이터 생성
2. **도서 조회** → 생성된 도서 확인
3. **도서 수정** → 도서 정보 업데이트
4. **도서 검색** → 다양한 검색 기능 테스트
5. **재고 관리** → 가용성 상태 변경 테스트
6. **통계 조회** → 현재 상태 확인
7. **도서 삭제** → Soft Delete 테스트
8. **도서 복원** → 삭제된 도서 복원 테스트

## 환경 변수

컬렉션에서 사용하는 주요 환경 변수:

- `baseUrl`: Spring Boot 애플리케이션 URL (기본값: http://localhost:8080)
- `testBookId`: 테스트용 도서 ID (기본값: 1)
- `testIsbn`: 테스트용 ISBN (기본값: 9780132350884)

## 예상 응답 형태

### 성공 응답 (도서 생성/조회)
```json
{
  "id": 1,
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "isbn": "9780132350884",
  "price": 45000,
  "available": true,
  "createdDate": "2025-09-27T11:00:00",
  "updatedDate": null,
  "deletedDate": null
}
```

### 페이징 응답 (도서 목록)
```json
{
  "content": [...],
  "pageable": {...},
  "totalElements": 10,
  "totalPages": 1,
  "last": true,
  "first": true,
  "size": 10,
  "number": 0
}
```

### 에러 응답 (400/500)
```json
{
  "timestamp": "2025-09-27T11:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/books"
}
```

## 주의사항

1. **테스트 순서**: 도서를 먼저 생성한 후 다른 API들을 테스트하세요
2. **데이터 초기화**: H2 인메모리 DB 사용 시 애플리케이션 재시작하면 데이터가 초기화됩니다
3. **ISBN 중복**: 같은 ISBN으로 여러 번 생성 시도하면 중복 에러가 발생합니다
4. **ID 값**: 실제 생성된 도서의 ID를 사용하여 조회/수정/삭제 테스트를 진행하세요

## 문제 해결

### 연결 오류
- Spring Boot 애플리케이션이 실행 중인지 확인
- 포트 8080이 사용 가능한지 확인

### 404 에러
- URL 경로가 올바른지 확인
- 실제 존재하는 도서 ID를 사용하는지 확인

### 400 에러
- 요청 본문의 JSON 형식이 올바른지 확인
- 필수 필드가 누락되지 않았는지 확인