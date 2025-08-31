package com.example.spring.service.impl;

import com.example.spring.dto.request.CreateMemberRequest;
import com.example.spring.dto.request.UpdateMemberRequest;
import com.example.spring.dto.response.MemberLoanLimitInfo;
import com.example.spring.dto.response.MemberResponse;
import com.example.spring.entity.Member;
import com.example.spring.entity.MembershipType;
import com.example.spring.event.MemberRegisteredEvent;
import com.example.spring.event.MembershipUpgradedEvent;
import com.example.spring.exception.DuplicateEmailException;
import com.example.spring.exception.EntityNotFoundException;
import com.example.spring.exception.MembershipUpgradeException;
import com.example.spring.repository.LoanRepository;
import com.example.spring.repository.MemberRepository;
import com.example.spring.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 회원 관리 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final LoanRepository loanRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public MemberResponse createMember(CreateMemberRequest request) {
        log.info("회원 생성 요청 - 이메일: {}, 이름: {}", request.getEmail(), request.getName());
        
        // 1. 이메일 중복 검증
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }
        
        // 2. Member 엔티티 생성
        Member member = Member.builder()
                .name(request.getName())
                .email(request.getEmail())
                .membershipType(request.getMembershipType() != null ? 
                              request.getMembershipType() : MembershipType.REGULAR)
                .joinDate(LocalDateTime.now())
                .build();
        
        // 3. 데이터베이스 저장
        Member savedMember = memberRepository.save(member);
        
        // 4. 회원가입 이벤트 발행 (환영 이메일 발송용)
        eventPublisher.publishEvent(new MemberRegisteredEvent(savedMember));
        
        log.info("회원 생성 완료 - ID: {}, 이메일: {}", savedMember.getId(), savedMember.getEmail());
        return MemberResponse.from(savedMember);
    }

    @Override
    @Transactional
    @CacheEvict(value = "members", key = "#id")
    public MemberResponse updateMember(Long id, UpdateMemberRequest request) {
        log.info("회원 정보 수정 요청 - ID: {}", id);
        
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Member", id));
        
        // 이메일 변경 시 중복 체크
        if (StringUtils.hasText(request.getEmail()) && 
            !request.getEmail().equals(member.getEmail())) {
            
            if (memberRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateEmailException(request.getEmail());
            }
            member.setEmail(request.getEmail());
        }
        
        // 이름 변경
        if (StringUtils.hasText(request.getName())) {
            member.setName(request.getName());
        }
        
        Member updatedMember = memberRepository.save(member);
        
        log.info("회원 정보 수정 완료 - ID: {}", updatedMember.getId());
        return MemberResponse.from(updatedMember);
    }

    @Override
    @Cacheable(value = "members", key = "#id")
    public MemberResponse findMemberById(Long id) {
        log.debug("회원 조회 - ID: {}", id);
        
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Member", id));
        
        return MemberResponse.from(member);
    }

    @Override
    public Page<MemberResponse> findAllMembers(Pageable pageable) {
        log.debug("전체 회원 목록 조회 - 페이지: {}, 크기: {}", 
                 pageable.getPageNumber(), pageable.getPageSize());
        
        List<MemberResponse> responses = memberRepository.findAll()
                .stream()
                .map(MemberResponse::from)
                .collect(Collectors.toList());
        
        return new PageImpl<>(responses, pageable, responses.size());
    }

    @Override
    @Transactional
    @CacheEvict(value = "members", key = "#id")
    public void deleteMember(Long id) {
        log.info("회원 삭제 요청 - ID: {}", id);
        
        if (!memberRepository.findById(id).isPresent()) {
            throw new EntityNotFoundException("Member", id);
        }
        
        // TODO: 대여 중인 도서가 있는지 확인하는 로직 추가 필요
        memberRepository.deleteById(id);
        
        log.info("회원 삭제 완료 - ID: {}", id);
    }

    @Override
    public List<MemberResponse> findMembersByName(String name) {
        log.debug("이름으로 회원 검색 - 검색어: {}", name);
        
        return memberRepository.findByNameContaining(name)
                .stream()
                .map(MemberResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<MemberResponse> findMembersByMembershipType(MembershipType type) {
        log.debug("멤버십 타입별 회원 조회 - 타입: {}", type);
        
        return memberRepository.findByMembershipType(type)
                .stream()
                .map(MemberResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CacheEvict(value = "members", key = "#memberId")
    public void upgradeMembership(Long memberId, MembershipType newType) {
        log.info("멤버십 업그레이드 요청 - 회원ID: {}, 대상타입: {}", memberId, newType);
        
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member", memberId));
        
        MembershipType currentType = member.getMembershipType();
        
        // 업그레이드 가능성 검증
        if (!isUpgradeAllowed(currentType, newType)) {
            throw new MembershipUpgradeException(currentType, newType);
        }
        
        // 멤버십 업그레이드
        member.setMembershipType(newType);
        Member upgradedMember = memberRepository.save(member);
        
        // 멤버십 업그레이드 이벤트 발행
        eventPublisher.publishEvent(new MembershipUpgradedEvent(upgradedMember, currentType, newType));
        
        log.info("멤버십 업그레이드 완료 - 회원ID: {}, {}에서 {}로 변경", 
                memberId, currentType, newType);
    }

    @Override
    public boolean validateEmailDuplicate(String email) {
        boolean exists = memberRepository.existsByEmail(email);
        log.debug("이메일 중복 검증 - 이메일: {}, 존재여부: {}", email, exists);
        return !exists; // true: 사용가능, false: 중복
    }

    @Override
    public MemberLoanLimitInfo getMemberLoanLimitInfo(Long memberId) {
        log.debug("회원 대여 제한 정보 조회 - 회원ID: {}", memberId);
        
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member", memberId));
        
        // 현재 대여 중인 도서 수 조회
        int currentLoanCount = loanRepository.findByMemberIdAndReturnDateIsNull(memberId).size();
        
        return MemberLoanLimitInfo.of(
                memberId, 
                member.getName(), 
                member.getMembershipType(), 
                currentLoanCount
        );
    }
    
    /**
     * 멤버십 업그레이드 가능 여부 확인
     */
    private boolean isUpgradeAllowed(MembershipType currentType, MembershipType newType) {
        // SUSPENDED는 업그레이드 불가
        if (currentType == MembershipType.SUSPENDED) {
            return false;
        }
        
        // 동일한 타입으로는 업그레이드 불가
        if (currentType == newType) {
            return false;
        }
        
        // REGULAR -> PREMIUM만 허용 (다운그레이드는 별도 처리 필요)
        return currentType == MembershipType.REGULAR && newType == MembershipType.PREMIUM;
    }
}