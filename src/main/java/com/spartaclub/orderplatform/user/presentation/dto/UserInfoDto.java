package com.spartaclub.orderplatform.user.presentation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 사용자 정보 공통 DTO
 * 여러 응답 DTO에서 사용되는 사용자 정보를 통합한 클래스
 *
 * @author 전우선
 * @date 2025-10-04(토)
 */
@Getter
@Setter
@NoArgsConstructor
public class UserInfoDto {

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

    // 마지막 수정일 (업데이트 응답에서 사용)
    private LocalDateTime modifiedAt;
}