package com.spartaclub.orderplatform.domain.user.domain.service;

/**
 * 토큰 서비스 인터페이스
 * 도메인 계층의 토큰 정책을 정의
 * 구체적인 토큰 방식(JWT, OAuth 등)은 Infrastructure 계층에서 구현
 */
public interface TokenService {

    /**
     * 액세스 토큰 생성
     * 
     * @param userId 사용자 ID
     * @param email 사용자 이메일
     * @param role 사용자 권한
     * @return 액세스 토큰
     */
    String createAccessToken(Long userId, String email, String role);

    /**
     * 리프레시 토큰 생성
     * 
     * @param userId 사용자 ID
     * @return 리프레시 토큰
     */
    String createRefreshToken(Long userId);

    /**
     * 토큰 유효성 검증
     * 
     * @param token 검증할 토큰
     * @return 유효성 여부
     */
    boolean validateToken(String token);

    /**
     * 리프레시 토큰인지 확인
     * 
     * @param token 확인할 토큰
     * @return 리프레시 토큰 여부
     */
    boolean isRefreshToken(String token);

    /**
     * 액세스 토큰 만료 시간(초) 반환
     * 
     * @return 만료 시간(초)
     */
    long getAccessTokenExpirationInSeconds();

    /**
     * 리프레시 토큰 만료 시간(밀리초) 반환
     * 
     * @return 만료 시간(밀리초)
     */
    long getRefreshTokenExpiration();
}