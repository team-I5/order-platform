package com.spartaclub.orderplatform.domain.user.domain.repository;

import com.spartaclub.orderplatform.domain.user.domain.entity.RefreshToken;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;

import java.util.Optional;

/**
 * RefreshToken 도메인 레포지토리 인터페이스
 * 도메인 계층에서 필요한 RefreshToken 관련 데이터 접근 메서드 정의
 * JPA에 의존하지 않는 순수 도메인 인터페이스
 *
 * @author 전우선
 * @date 2025-10-15
 */
public interface RefreshTokenRepository {

    // 기본 CRUD
    RefreshToken save(RefreshToken refreshToken);
    Optional<RefreshToken> findByToken(String token);
    void delete(RefreshToken refreshToken);
    
    // 활성 토큰 조회 (삭제되지 않은)
    Optional<RefreshToken> findActiveByToken(String token);

    // 사용자별 토큰 조회
    Optional<RefreshToken> findByUser(User user);
    Optional<RefreshToken> findByUserId(Long userId);

    // 토큰 삭제
    void deleteByUser(User user);
    void deleteByUserId(Long userId);
    void deleteByToken(String token);
}