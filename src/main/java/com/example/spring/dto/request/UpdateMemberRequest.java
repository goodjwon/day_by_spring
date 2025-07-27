package com.example.spring.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 회원 정보 수정 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMemberRequest {
    
    @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하여야 합니다")
    private String name;
    
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;
}