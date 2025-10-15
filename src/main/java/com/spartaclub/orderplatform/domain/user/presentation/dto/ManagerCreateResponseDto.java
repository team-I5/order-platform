package com.spartaclub.orderplatform.domain.user.presentation.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 관리자 계정 생성 응답 DTO MASTER가 MANAGER 계정 생성 시 반환되는 정보
 *
 * @author 전우선
 * @date 2025-10-08(수)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagerCreateResponseDto {

    // 성공 메시지
    private String message;

    // 생성된 사용자 정보
    private UserInfo user;

    // 생성한 MASTER 이메일
    private String createdBy;

    /**
     * 생성된 사용자 정보 내부 클래스
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserInfo {

        private Long userId;
        private String username;
        private String email;
        private String nickname;
        private String phoneNumber;
        private String businessNumber; // MANAGER는 항상 null
        private String role;
        private LocalDateTime createdAt;
    }
}