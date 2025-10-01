package com.spartaclub.orderplatform.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 회원가입 응답 DTO 클래스
 * 회원가입 성공 시 클라이언트에게 반환할 데이터
 * 
 * @author 전우선
 * @date 2025-10-01(수)
 */
@Getter
@AllArgsConstructor
public class UserSignupResponseDto {

    private String message;
    private Long userId;

    /**
     * 회원가입 성공 응답 생성 정적 메서드
     * 
     * @param userId 생성된 사용자 ID
     * @return 회원가입 성공 응답 DTO
     */
    public static UserSignupResponseDto success(Long userId) {
        return new UserSignupResponseDto("회원가입이 완료되었습니다.", userId);
    }
}