package com.spartaclub.orderplatform.domain.user.domain.repository;

import com.spartaclub.orderplatform.domain.user.domain.entity.RefreshToken;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;

import java.util.Optional;

/**
 * RefreshToken 도메인 레포지토리 인터페이스
 * 도메인 계층의 추상화된 저장소 인터페이스
 */
public interface RefreshTokenDomainRepository {

    RefreshToken save(RefreshToken refreshToken);
    
    Optional<RefreshToken> findActiveTokenByValue(String tokenValue);
    
    void deleteByUser(User user);
    
    void delete(RefreshToken refreshToken);
}