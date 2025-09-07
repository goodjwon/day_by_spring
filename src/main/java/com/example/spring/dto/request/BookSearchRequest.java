package com.example.spring.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 도서 검색 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookSearchRequest {

    private String title;
    
    private String author;
    
    private String keyword;
    
    @DecimalMin(value = "0.0", message = "최소 가격은 0 이상이어야 합니다")
    @Digits(integer = 8, fraction = 2, message = "가격 형식이 올바르지 않습니다")
    private BigDecimal minPrice;
    
    @DecimalMin(value = "0.0", message = "최대 가격은 0 이상이어야 합니다")
    @Digits(integer = 8, fraction = 2, message = "가격 형식이 올바르지 않습니다")
    private BigDecimal maxPrice;
    
    private Boolean available;
}