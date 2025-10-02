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
    private UserInfo user;

    /**
     * 사용자 정보 내부 클래스
     * 로그인 응답에 포함될 사용자 기본 정보
     * 민감 정보(비밀번호 등)는 제외하고 필요한 정보만 포함
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class UserInfo {
        // 사용자 고유 ID
        private Long userId;

        // 사용자명
        private String username;

        // 이메일 주소
        private String email;

        // 닉네임
        private String nickname;

        // 연락처
        private String phoneNumber;

        // 사용자 권한 (CUSTOMER, OWNER 등)
        private String role;

        /**
         * User 엔티티로부터 UserInfo 객체 생성
         * 엔티티의 정보를 DTO로 변환
         *
         * @param user User 엔티티 객체
         */
        public UserInfo(User user) {
            this.userId = user.getUserId();
            this.username = user.getUsername();
            this.email = user.getEmail();
            this.nickname = user.getNickname();
            this.phoneNumber = user.getPhoneNumber();
            this.role = user.getRole().name(); // Enum을 문자열로 변환
        }
    }

    /**
     * 로그인 성공 응답 DTO 생성자
     * 로그인 성공 시 필요한 모든 정보를 설정
     *
     * @param accessToken  JWT 액세스 토큰
     * @param refreshToken JWT 리프레시 토큰
     * @param expiresIn    액세스 토큰 만료 시간(초)
     * @param user         로그인한 사용자 엔티티
     */
    public UserLoginResponseDto(String accessToken, String refreshToken, long expiresIn, User user) {
        this.message = "로그인 성공";
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = "Bearer"; // Bearer 토큰 타입 고정
        this.expiresIn = expiresIn;
        this.user = new UserInfo(user); // User 엔티티를 UserInfo DTO로 변환
    }
}