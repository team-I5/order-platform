package com.spartaclub.orderplatform.domain.user.infrastructure.service;

import com.spartaclub.orderplatform.domain.user.domain.service.TokenService;
import com.spartaclub.orderplatform.global.application.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * JWT 기반 토큰 서비스 구현체
 * Infrastructure 계층에서 구체적인 토큰 방식을 구현
 */
@Service
@RequiredArgsConstructor
public class JwtTokenService implements TokenService {

    private final JwtUtil jwtUtil;

    @Override
    public String createAccessToken(Long userId, String email, String role) {
        return jwtUtil.createAccessToken(userId, email, role);
    }

    @Override
    public String createRefreshToken(Long userId) {
        return jwtUtil.createRefreshToken(userId);
    }

    @Override
    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    @Override
    public boolean isRefreshToken(String token) {
        return jwtUtil.isRefreshToken(token);
    }

    @Override
    public long getAccessTokenExpirationInSeconds() {
        return jwtUtil.getAccessTokenExpirationInSeconds();
    }

    @Override
    public long getRefreshTokenExpiration() {
        return jwtUtil.getRefreshTokenExpiration();
    }
}