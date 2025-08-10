package com.example.spring.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 전역 예외 처리기
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 비즈니스 공통
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (ex instanceof EntityNotFoundException) {
            status = HttpStatus.NOT_FOUND;
        } else if (ex instanceof DuplicateEmailException) {
            status = HttpStatus.CONFLICT;
        }
        log.warn("Business exception: code={}, message={}", ex.getErrorCode(), ex.getMessage());
        return buildErrorResponse(status, ex.getErrorCode(), ex.getMessage(), request.getRequestURI());
    }

    // @Valid 바디 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                      HttpServletRequest request) {
        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> ErrorResponse.FieldError.builder()
                        .field(err.getField())
                        .rejectedValue(safeToString(err.getRejectedValue()))
                        .message(err.getDefaultMessage())
                        .build())
                .collect(Collectors.toList());
        log.debug("Validation failed: {} errors", fieldErrors.size());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED",
                "요청 데이터가 유효하지 않습니다.", request.getRequestURI(), fieldErrors);
    }

    // 폼/쿼리 파라미터 바인딩 실패
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException ex, HttpServletRequest request) {
        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> ErrorResponse.FieldError.builder()
                        .field(err.getField())
                        .rejectedValue(safeToString(err.getRejectedValue()))
                        .message(err.getDefaultMessage())
                        .build())
                .collect(Collectors.toList());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "BINDING_FAILED",
                "요청 파라미터 바인딩에 실패했습니다.", request.getRequestURI(), fieldErrors);
    }

    // @Validated on @RequestParam, @PathVariable 등
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
                                                                   HttpServletRequest request) {
        List<ErrorResponse.FieldError> violations = ex.getConstraintViolations().stream()
                .map(v -> ErrorResponse.FieldError.builder()
                        .field(v.getPropertyPath().toString())
                        .rejectedValue(safeToString(v.getInvalidValue()))
                        .message(v.getMessage())
                        .build())
                .collect(Collectors.toList());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "CONSTRAINT_VIOLATION",
                "요청 값 제약 조건을 위반했습니다.", request.getRequestURI(), violations);
    }

    @ExceptionHandler({MissingServletRequestParameterException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorResponse> handleBadRequests(Exception ex, HttpServletRequest request) {
        log.debug("Bad request: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage(), request.getRequestURI());
    }

    // 알 수 없는 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnhandled(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                "서버 내부 오류가 발생했습니다.", request.getRequestURI());
    }

    // Helper
    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String code, String message, String path) {
        return buildErrorResponse(status, code, message, path, List.of());
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String code, String message,
                                                             String path, List<ErrorResponse.FieldError> fieldErrors) {
        ErrorResponse body = ErrorResponse.builder()
                .timestamp(OffsetDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .code(code)
                .message(message)
                .path(path)
                .errors(fieldErrors == null || fieldErrors.isEmpty() ? null : fieldErrors)
                .build();
        return ResponseEntity.status(status).body(body);
    }

    private String safeToString(Object value) {
        if (value == null) return null;
        try {
            return String.valueOf(value);
        } catch (Exception e) {
            return "<unprintable>";
        }
    }
}
