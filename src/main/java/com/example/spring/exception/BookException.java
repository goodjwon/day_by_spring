package com.example.spring.exception;

/**
 * 도서 관련 예외 클래스들
 */
public class BookException {

    /**
     * ISBN 중복 예외
     */
    public static class DuplicateIsbnException extends BusinessException {
        public DuplicateIsbnException(String isbn) {
            super("DUPLICATE_ISBN", "이미 존재하는 ISBN입니다: " + isbn);
        }
    }

    /**
     * 도서를 찾을 수 없는 예외
     */
    public static class BookNotFoundException extends BusinessException {
        public BookNotFoundException(Long id) {
            super("BOOK_NOT_FOUND", "도서를 찾을 수 없습니다. ID: " + id);
        }
        
        public BookNotFoundException(String isbn) {
            super("BOOK_NOT_FOUND", "도서를 찾을 수 없습니다. ISBN: " + isbn);
        }
    }

    /**
     * 삭제된 도서 접근 예외
     */
    public static class DeletedBookAccessException extends BusinessException {
        public DeletedBookAccessException(Long id) {
            super("DELETED_BOOK_ACCESS", "삭제된 도서에 접근할 수 없습니다. ID: " + id);
        }
    }

    /**
     * 도서 재고 부족 예외
     */
    public static class BookNotAvailableException extends BusinessException {
        public BookNotAvailableException(Long id) {
            super("BOOK_NOT_AVAILABLE", "도서가 재고가 없습니다. ID: " + id);
        }
        
        public BookNotAvailableException(String title) {
            super("BOOK_NOT_AVAILABLE", "도서가 재고가 없습니다. 제목: " + title);
        }
    }

    /**
     * 잘못된 ISBN 형식 예외
     */
    public static class InvalidIsbnException extends BusinessException {
        public InvalidIsbnException(String isbn) {
            super("INVALID_ISBN", "잘못된 ISBN 형식입니다: " + isbn);
        }
    }

    /**
     * 도서 가격 유효성 예외
     */
    public static class InvalidBookPriceException extends BusinessException {
        public InvalidBookPriceException(String message) {
            super("INVALID_BOOK_PRICE", "잘못된 도서 가격입니다: " + message);
        }
    }
}