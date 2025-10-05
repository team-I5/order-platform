package com.spartaclub.orderplatform.user.presentation.dto;

import com.spartaclub.orderplatform.user.domain.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 로그인 응답 DTO
 * 로그인 성공 시 클라이언트에게 반환되는 데이터 전송 객체
 * JWT 토큰 정보와 사용자 기본 정보 포함
 *
 * @author 전우선
 * @date 2025-10-02(목)
 */
@Getter
@Setter
@NoArgsConstructor
public class UserLoginResponseDto {

    // 로그인 성공 메시지
    private String message;

    // JWT 액세스 토큰 (API 인증용)
    private String accessToken;

    // JWT 리프레시 토큰 (토큰 갱신용)
    private String refreshToken;

    // 토큰 타입 (Bearer 고정)
    private String tokenType;

    // 액세스 토큰 만료 시간 (초 단위)
    private long expiresIn;

    // 로그인한 사용자 기본 정보
    private UserInfoDto user;


    /**
     * 로그인 성공 응답 DTO 생성자
     * 로그인 성공 시 필요한 모든 정보를 설정
     *
     * @param accessToken  JWT 액세스 토큰
     * @param refreshToken JWT 리프레시 토큰
     * @param expiresIn    액세스 토큰 만료 시간(초)
     * @param userInfo     로그인한 사용자 정보 DTO
     */
    public UserLoginResponseDto(String accessToken, String refreshToken, long expiresIn, UserInfoDto userInfo) {
        this.message = "로그인 성공";
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = "Bearer"; // Bearer 토큰 타입 고정
        this.expiresIn = expiresIn;
        this.user = userInfo; // MapStruct로 변환된 UserInfo DTO 사용
    }
}