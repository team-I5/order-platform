package com.spartaclub.orderplatform.domain.user.presentation.dto;

import com.spartaclub.orderplatform.domain.user.domain.entity.UserRole;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 회원 전체 조회 요청 DTO 관리자용 회원 목록 조회 시 검색/필터링/정렬 조건을 담는 클래스
 *
 * @author 전우선
 * @date 2025-10-08(수)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserListRequestDto {

    // 권한 필터 (CUSTOMER, OWNER, MANAGER, MASTER)
    private UserRole role;

    // 탈퇴한 회원 포함 여부 (기본값: false)
    @Builder.Default
    private Boolean includeDeleted = false;

    // 검색어 (username, email, nickname 부분 일치)
    private String keyword;

    // 가입일 시작 (YYYY-MM-DD)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    // 가입일 종료 (YYYY-MM-DD)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
}