package com.example.spring.controller;

import com.example.spring.dto.request.CreateMemberRequest;
import com.example.spring.dto.request.UpdateMemberRequest;
import com.example.spring.dto.response.MemberLoanLimitInfo;
import com.example.spring.dto.response.MemberResponse;
import com.example.spring.entity.MembershipType;
import com.example.spring.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = MemberController.class)
class MemberControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MemberService memberService;

    private MemberResponse sampleMember(Long id) {
        return MemberResponse.builder()
                .id(id)
                .name("John Doe")
                .email("john.doe@example.com")
                .membershipType(MembershipType.REGULAR)
                .joinDate(LocalDateTime.of(2024, 1, 1, 10, 0))
                .build();
    }

    @Test
    @DisplayName("POST /api/members - 회원 생성 성공시 201과 응답 본문 반환")
    void createMember_success() throws Exception {
        CreateMemberRequest request = CreateMemberRequest.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .membershipType(MembershipType.REGULAR)
                .build();

        MemberResponse response = sampleMember(1L);
        Mockito.when(memberService.createMember(any(CreateMemberRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")))
                .andExpect(jsonPath("$.membershipType", is("REGULAR")));
    }

    @Test
    @DisplayName("POST /api/members - 유효성 검증 실패시 400 반환")
    void createMember_validationFail() throws Exception {
        // name, email 누락
        String invalidJson = "{" +
                "\"membershipType\": \"REGULAR\"" +
                "}";

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/members/{id} - 회원 상세 조회 200")
    void getMember_success() throws Exception {
        Mockito.when(memberService.findMemberById(1L)).thenReturn(sampleMember(1L));

        mockMvc.perform(get("/api/members/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Doe")));
    }

    @Test
    @DisplayName("GET /api/members - 회원 목록 조회 200")
    void getMembers_success() throws Exception {
        Mockito.when(memberService.findAllMembers(any())).thenReturn(List.of(sampleMember(1L), sampleMember(2L)));

        mockMvc.perform(get("/api/members")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    @DisplayName("PUT /api/members/{id} - 회원 정보 수정 200")
    void updateMember_success() throws Exception {
        UpdateMemberRequest request = UpdateMemberRequest.builder()
                .name("Jane Doe")
                .email("jane.doe@example.com")
                .build();

        MemberResponse updated = MemberResponse.builder()
                .id(1L)
                .name("Jane Doe")
                .email("jane.doe@example.com")
                .membershipType(MembershipType.REGULAR)
                .joinDate(LocalDateTime.of(2024, 1, 1, 10, 0))
                .build();

        Mockito.when(memberService.updateMember(eq(1L), any(UpdateMemberRequest.class))).thenReturn(updated);

        mockMvc.perform(put("/api/members/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Jane Doe")))
                .andExpect(jsonPath("$.email", is("jane.doe@example.com")));
    }

    @Test
    @DisplayName("DELETE /api/members/{id} - 회원 삭제 204")
    void deleteMember_success() throws Exception {
        mockMvc.perform(delete("/api/members/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(memberService, times(1)).deleteMember(1L);
    }

    @Test
    @DisplayName("GET /api/members/search?name= - 이름 검색 200")
    void searchMembersByName_success() throws Exception {
        Mockito.when(memberService.findMembersByName("john"))
                .thenReturn(List.of(sampleMember(1L)));

        mockMvc.perform(get("/api/members/search").param("name", "john"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("John Doe")));
    }

    @Test
    @DisplayName("GET /api/members/membership/{type} - 멤버십 타입별 조회 200")
    void getMembersByMembershipType_success() throws Exception {
        Mockito.when(memberService.findMembersByMembershipType(MembershipType.PREMIUM))
                .thenReturn(List.of(sampleMember(3L)));

        mockMvc.perform(get("/api/members/membership/{type}", "PREMIUM"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(3)))
                .andExpect(jsonPath("$[0].membershipType", is("REGULAR"))); // sampleMember uses REGULAR
    }

    @Test
    @DisplayName("PUT /api/members/{id}/membership?membershipType= - 멤버십 업그레이드 200")
    void upgradeMembership_success() throws Exception {
        mockMvc.perform(put("/api/members/{id}/membership", 1L)
                        .param("membershipType", "PREMIUM"))
                .andExpect(status().isOk());

        verify(memberService, times(1)).upgradeMembership(1L, MembershipType.PREMIUM);
    }

    @Test
    @DisplayName("GET /api/members/email/validate?email= - 이메일 중복 확인 200")
    void validateEmail_success() throws Exception {
        Mockito.when(memberService.validateEmailDuplicate("a@b.com")).thenReturn(true);

        mockMvc.perform(get("/api/members/email/validate").param("email", "a@b.com"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("GET /api/members/{id}/loan-limit - 대여 제한 정보 200")
    void getMemberLoanLimitInfo_success() throws Exception {
        MemberLoanLimitInfo info = MemberLoanLimitInfo.of(1L, "John Doe", MembershipType.REGULAR, 2);
        Mockito.when(memberService.getMemberLoanLimitInfo(1L)).thenReturn(info);

        mockMvc.perform(get("/api/members/{id}/loan-limit", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.memberId", is(1)))
                .andExpect(jsonPath("$.memberName", is("John Doe")))
                .andExpect(jsonPath("$.membershipType", is("REGULAR")))
                .andExpect(jsonPath("$.maxLoanCount", is(5)))
                .andExpect(jsonPath("$.currentLoanCount", is(2)))
                .andExpect(jsonPath("$.remainingLoanCount", is(3)))
                .andExpect(jsonPath("$.canLoan", is(true)));
    }
}
