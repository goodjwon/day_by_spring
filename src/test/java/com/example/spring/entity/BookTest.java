package com.example.spring.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Book 엔티티 테스트")
class BookTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Nested
    @DisplayName("Book 생성 테스트")
    class CreateBookTest {

        @Test
        @DisplayName("유효한 Book 객체 생성 성공")
        void createValidBook() {
            // given
            Book book = Book.builder()
                    .title("Clean Code")
                    .author("Robert C. Martin")
                    .isbn("9780132350884")
                    .price(new BigDecimal("45.99"))
                    .build();

            // when
            Set<ConstraintViolation<Book>> violations = validator.validate(book);

            // then
            assertThat(violations).isEmpty();
            assertThat(book.getTitle()).isEqualTo("Clean Code");
            assertThat(book.getAuthor()).isEqualTo("Robert C. Martin");
            assertThat(book.getIsbn()).isEqualTo("9780132350884");
            assertThat(book.getPrice()).isEqualByComparingTo(new BigDecimal("45.99"));
            assertThat(book.getAvailable()).isTrue(); // Default value
        }

        @Test
        @DisplayName("Builder 패턴으로 Book 생성")
        void createBookWithBuilder() {
            // given & when
            Book book = Book.builder()
                    .title("Effective Java")
                    .author("Joshua Bloch")
                    .isbn("9780134685991")
                    .price(new BigDecimal("52.99"))
                    .available(true)
                    .createdDate(LocalDateTime.now())
                    .build();

            // then
            assertThat(book.getTitle()).isEqualTo("Effective Java");
            assertThat(book.getAuthor()).isEqualTo("Joshua Bloch");
            assertThat(book.getIsbn()).isEqualTo("9780134685991");
            assertThat(book.getPrice()).isEqualByComparingTo(new BigDecimal("52.99"));
            assertThat(book.getAvailable()).isTrue();
            assertThat(book.getCreatedDate()).isNotNull();
        }

        @Test
        @DisplayName("기본값 설정 확인")
        void checkDefaultValues() {
            // given & when
            Book book = Book.builder()
                    .title("Test Book")
                    .author("Test Author")
                    .isbn("9781234567890")
                    .price(new BigDecimal("25.00"))
                    .build();

            // then
            assertThat(book.getAvailable()).isTrue();
            assertThat(book.isDeleted()).isFalse();
            assertThat(book.getDeletedDate()).isNull();
        }
    }

    @Nested
    @DisplayName("Bean Validation 테스트")
    class ValidationTest {

        @Test
        @DisplayName("제목이 null이면 검증 실패")
        void titleShouldNotBeNull() {
            // given
            Book book = Book.builder()
                    .title(null)
                    .author("Test Author")
                    .isbn("9781234567890")
                    .price(new BigDecimal("25.00"))
                    .build();

            // when
            Set<ConstraintViolation<Book>> violations = validator.validate(book);

            // then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("도서 제목은 필수입니다");
        }

        @Test
        @DisplayName("제목이 빈 문자열이면 검증 실패")
        void titleShouldNotBeBlank() {
            // given
            Book book = Book.builder()
                    .title("")
                    .author("Test Author")
                    .isbn("9781234567890")
                    .price(new BigDecimal("25.00"))
                    .build();

            // when
            Set<ConstraintViolation<Book>> violations = validator.validate(book);

            // then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("도서 제목은 필수입니다");
        }

        @Test
        @DisplayName("저자가 null이면 검증 실패")
        void authorShouldNotBeNull() {
            // given
            Book book = Book.builder()
                    .title("Test Book")
                    .author(null)
                    .isbn("9781234567890")
                    .price(new BigDecimal("25.00"))
                    .build();

            // when
            Set<ConstraintViolation<Book>> violations = validator.validate(book);

            // then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("저자는 필수입니다");
        }

        @Test
        @DisplayName("ISBN이 null이면 검증 실패")
        void isbnShouldNotBeNull() {
            // given
            Book book = Book.builder()
                    .title("Test Book")
                    .author("Test Author")
                    .isbn(null)
                    .price(new BigDecimal("25.00"))
                    .build();

            // when
            Set<ConstraintViolation<Book>> violations = validator.validate(book);

            // then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("ISBN은 필수입니다");
        }

        @Test
        @DisplayName("잘못된 ISBN 형식이면 검증 실패")
        void invalidIsbnFormatShouldFail() {
            // given
            Book book = Book.builder()
                    .title("Test Book")
                    .author("Test Author")
                    .isbn("invalid-isbn")
                    .price(new BigDecimal("25.00"))
                    .build();

            // when
            Set<ConstraintViolation<Book>> violations = validator.validate(book);

            // then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("올바른 ISBN 형식이 아닙니다");
        }

        @Test
        @DisplayName("유효한 ISBN-13 형식 검증 성공")
        void validIsbn13FormatShouldPass() {
            // given
            Book book = Book.builder()
                    .title("Test Book")
                    .author("Test Author")
                    .isbn("9781234567890")
                    .price(new BigDecimal("25.00"))
                    .build();

            // when
            Set<ConstraintViolation<Book>> violations = validator.validate(book);

            // then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("하이픈이 포함된 ISBN-13 형식 검증 성공")
        void validIsbn13WithHyphensShouldPass() {
            // given
            Book book = Book.builder()
                    .title("Test Book")
                    .author("Test Author")
                    .isbn("978-0-13-235088-4")
                    .price(new BigDecimal("25.00"))
                    .build();

            // when
            Set<ConstraintViolation<Book>> violations = validator.validate(book);

            // then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("가격이 null이면 검증 실패")
        void priceShouldNotBeNull() {
            // given
            Book book = Book.builder()
                    .title("Test Book")
                    .author("Test Author")
                    .isbn("9781234567890")
                    .price(null)
                    .build();

            // when
            Set<ConstraintViolation<Book>> violations = validator.validate(book);

            // then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("가격은 필수입니다");
        }

        @Test
        @DisplayName("가격이 음수이면 검증 실패")
        void priceShouldNotBeNegative() {
            // given
            Book book = Book.builder()
                    .title("Test Book")
                    .author("Test Author")
                    .isbn("9781234567890")
                    .price(new BigDecimal("-10.00"))
                    .build();

            // when
            Set<ConstraintViolation<Book>> violations = validator.validate(book);

            // then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("가격은 0 이상이어야 합니다");
        }

        @Test
        @DisplayName("가격이 0이면 검증 성공")
        void priceCanBeZero() {
            // given
            Book book = Book.builder()
                    .title("Test Book")
                    .author("Test Author")
                    .isbn("9781234567890")
                    .price(BigDecimal.ZERO)
                    .build();

            // when
            Set<ConstraintViolation<Book>> violations = validator.validate(book);

            // then
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Soft Delete 기능 테스트")
    class SoftDeleteTest {

        @Test
        @DisplayName("생성된 Book은 삭제되지 않은 상태")
        void newBookShouldNotBeDeleted() {
            // given & when
            Book book = Book.builder()
                    .title("Test Book")
                    .author("Test Author")
                    .isbn("9781234567890")
                    .price(new BigDecimal("25.00"))
                    .build();

            // then
            assertThat(book.isDeleted()).isFalse();
            assertThat(book.getDeletedDate()).isNull();
            assertThat(book.getAvailable()).isTrue();
        }

        @Test
        @DisplayName("markAsDeleted() 호출 시 삭제 상태로 변경")
        void markAsDeletedShouldSetDeletedState() {
            // given
            Book book = Book.builder()
                    .title("Test Book")
                    .author("Test Author")
                    .isbn("9781234567890")
                    .price(new BigDecimal("25.00"))
                    .build();

            // when
            book.markAsDeleted();

            // then
            assertThat(book.isDeleted()).isTrue();
            assertThat(book.getDeletedDate()).isNotNull();
            assertThat(book.getAvailable()).isFalse();
        }

        @Test
        @DisplayName("restore() 호출 시 삭제 상태 해제")
        void restoreShouldClearDeletedState() {
            // given
            Book book = Book.builder()
                    .title("Test Book")
                    .author("Test Author")
                    .isbn("9781234567890")
                    .price(new BigDecimal("25.00"))
                    .build();
            book.markAsDeleted();

            // when
            book.restore();

            // then
            assertThat(book.isDeleted()).isFalse();
            assertThat(book.getDeletedDate()).isNull();
            assertThat(book.getAvailable()).isTrue();
        }

        @Test
        @DisplayName("삭제 후 복원 시 deletedDate가 정확히 초기화됨")
        void restoreShouldResetDeletedDate() {
            // given
            Book book = Book.builder()
                    .title("Test Book")
                    .author("Test Author")
                    .isbn("9781234567890")
                    .price(new BigDecimal("25.00"))
                    .build();

            // when
            book.markAsDeleted();
            LocalDateTime deletedDate = book.getDeletedDate();
            book.restore();

            // then
            assertThat(deletedDate).isNotNull();
            assertThat(book.getDeletedDate()).isNull();
            assertThat(book.isDeleted()).isFalse();
        }
    }

    @Nested
    @DisplayName("Audit 기능 테스트")
    class AuditTest {

        @Test
        @DisplayName("생성 시간과 수정 시간 필드 존재 확인")
        void auditFieldsExist() {
            // given & when
            Book book = Book.builder()
                    .title("Test Book")
                    .author("Test Author")
                    .isbn("9781234567890")
                    .price(new BigDecimal("25.00"))
                    .createdDate(LocalDateTime.now())
                    .updatedDate(LocalDateTime.now())
                    .build();

            // then
            assertThat(book.getCreatedDate()).isNotNull();
            assertThat(book.getUpdatedDate()).isNotNull();
        }

        @Test
        @DisplayName("생성 시간은 수정할 수 없도록 설정됨 (updatable = false)")
        void createdDateShouldNotBeUpdatable() {
            // given
            LocalDateTime initialCreatedDate = LocalDateTime.now().minusDays(1);
            Book book = Book.builder()
                    .title("Test Book")
                    .author("Test Author")
                    .isbn("9781234567890")
                    .price(new BigDecimal("25.00"))
                    .createdDate(initialCreatedDate)
                    .build();

            // when
            LocalDateTime newCreatedDate = LocalDateTime.now();
            book.setCreatedDate(newCreatedDate);

            // then
            // 실제 JPA 컨텍스트에서는 updatable=false로 인해 무시되지만,
            // 단순 객체 테스트에서는 setter가 동작함을 확인
            assertThat(book.getCreatedDate()).isEqualTo(newCreatedDate);
        }
    }

    @Nested
    @DisplayName("동등성 및 해시코드 테스트")
    class EqualsAndHashCodeTest {

        @Test
        @DisplayName("같은 내용의 Book 객체는 동등함")
        void booksWithSameContentShouldBeEqual() {
            // given
            Book book1 = Book.builder()
                    .id(1L)
                    .title("Test Book")
                    .author("Test Author")
                    .isbn("9781234567890")
                    .price(new BigDecimal("25.00"))
                    .build();

            Book book2 = Book.builder()
                    .id(1L)
                    .title("Test Book")
                    .author("Test Author")
                    .isbn("9781234567890")
                    .price(new BigDecimal("25.00"))
                    .build();

            // when & then
            assertThat(book1).isEqualTo(book2);
            assertThat(book1.hashCode()).isEqualTo(book2.hashCode());
        }

        @Test
        @DisplayName("다른 ID를 가진 Book 객체는 다름")
        void booksWithDifferentIdShouldNotBeEqual() {
            // given
            Book book1 = Book.builder()
                    .id(1L)
                    .title("Test Book")
                    .author("Test Author")
                    .isbn("9781234567890")
                    .price(new BigDecimal("25.00"))
                    .build();

            Book book2 = Book.builder()
                    .id(2L)
                    .title("Test Book")
                    .author("Test Author")
                    .isbn("9781234567890")
                    .price(new BigDecimal("25.00"))
                    .build();

            // when & then
            assertThat(book1).isNotEqualTo(book2);
        }
    }

    @Nested
    @DisplayName("toString 테스트")
    class ToStringTest {

        @Test
        @DisplayName("toString 메서드가 주요 필드를 포함함")
        void toStringShouldContainMainFields() {
            // given
            Book book = Book.builder()
                    .id(1L)
                    .title("Clean Code")
                    .author("Robert C. Martin")
                    .isbn("9780132350884")
                    .price(new BigDecimal("45.99"))
                    .build();

            // when
            String bookString = book.toString();

            // then
            assertThat(bookString).contains("Clean Code");
            assertThat(bookString).contains("Robert C. Martin");
            assertThat(bookString).contains("9780132350884");
            assertThat(bookString).contains("45.99");
        }
    }
}