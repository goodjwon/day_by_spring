package com.example.spring.service;

import com.example.spring.dto.request.CreateMemberRequest;
import com.example.spring.dto.request.UpdateMemberRequest;
import com.example.spring.dto.response.MemberLoanLimitInfo;
import com.example.spring.dto.response.MemberResponse;
import com.example.spring.entity.MembershipType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 회원 관리 서비스 인터페이스
 */
public interface MemberService {
    
    // 회원 관리
    MemberResponse createMember(CreateMemberRequest request);
    MemberResponse updateMember(Long id, UpdateMemberRequest request);
    MemberResponse findMemberById(Long id);
    Page<MemberResponse> findAllMembers(Pageable pageable);
    void deleteMember(Long id);
    
    // 회원 검색
    List<MemberResponse> findMembersByName(String name);
    List<MemberResponse> findMembersByMembershipType(MembershipType type);
    
    // 비즈니스 로직
    void upgradeMembership(Long memberId, MembershipType newType);
    boolean validateEmailDuplicate(String email);
    MemberLoanLimitInfo getMemberLoanLimitInfo(Long memberId);
}