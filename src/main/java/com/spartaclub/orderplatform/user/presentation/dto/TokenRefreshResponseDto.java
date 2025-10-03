package com.spartaclub.orderplatform.user.presentation.dto;

import com.spartaclub.orderplatform.user.domain.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 토큰 갱신 응답 DTO
 * 토큰 갱신 성공 시 클라이언트에게 반환되는 데이터 전송 객체
 * 새로운 JWT 토큰 정보와 사용자 기본 정보 포함
 *
 * @author 전우선
 * @date 2025-10-03(금)
 */
@Getter
@Setter
@NoArgsConstructor
public class TokenRefreshResponseDto {

    // 토큰 갱신 성공 메시지
    private String message;

    // 새로 발급된 JWT 액세스 토큰
    private String accessToken;

    // 새로 발급된 JWT 리프레시 토큰 (RTR 패턴)
    private String refreshToken;

    // 토큰 타입 (Bearer 고정)
    private String tokenType;

    // 액세스 토큰 만료 시간 (초 단위)
    private long expiresIn;

    // 토큰 갱신 요청한 사용자 기본 정보
    private UserInfo user;

    /**
     * 사용자 정보 내부 클래스
     * 토큰 갱신 응답에 포함될 사용자 기본 정보
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class UserInfo {
        private Long userId;
        private String username;
        private String email;
        private String phoneNumber;
        private String role;

        /**
         * User 엔티티로부터 UserInfo 객체 생성
         */
        public UserInfo(User user) {
            this.userId = user.getUserId();
            this.username = user.getUsername();
            this.email = user.getEmail();
            this.phoneNumber = user.getPhoneNumber();
            this.role = user.getRole().name();
        }
    }

    /**
     * 토큰 갱신 성공 응답 DTO 생성자
     *
     * @param accessToken  새로 발급된 액세스 토큰
     * @param refreshToken 새로 발급된 리프레시 토큰
     * @param expiresIn    액세스 토큰 만료 시간(초)
     * @param user         사용자 엔티티
     */
    public TokenRefreshResponseDto(String accessToken, String refreshToken, long expiresIn, User user) {
        this.message = "토큰이 갱신되었습니다.";
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = "Bearer";
        this.expiresIn = expiresIn;
        this.user = new UserInfo(user);
    }
}