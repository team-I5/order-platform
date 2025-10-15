package com.spartaclub.orderplatform.domain.user.presentation.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 회원 목록 응답 DTO
 * 관리자용 회원 목록에 포함될 개별 회원 정보 클래스
 *
 * @author 전우선
 * @date 2025-10-08(수)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserListResponseDto {

    // 사용자 고유 ID
    private Long userId;

    // 사용자명 (로그인 시 사용)
    private String username;

    // 이메일 주소
    private String email;

    // 닉네임 (화면 표시용)
    private String nickname;

    // 연락처
    private String phoneNumber;

    // 사업자번호 (사업자/관리자만)
    private String businessNumber;

    // 사용자 권한
    private String role;

    // 계정 생성일
    private LocalDateTime createdAt;

    // 마지막 수정일
    private LocalDateTime modifiedAt;

    // 탈퇴일 (탈퇴하지 않은 경우 null)
    private LocalDateTime deletedAt;

    // 활성 상태 (탈퇴 여부)
    private Boolean isActive;
}