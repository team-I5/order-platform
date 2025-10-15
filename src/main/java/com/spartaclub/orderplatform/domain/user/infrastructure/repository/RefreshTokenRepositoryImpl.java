package com.spartaclub.orderplatform.domain.user.infrastructure.repository;

import com.spartaclub.orderplatform.domain.user.domain.entity.RefreshToken;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.domain.user.domain.repository.RefreshTokenDomainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * RefreshTokenDomainRepository의 JPA 구현체
 * Infrastructure 계층에서 JPA 구현 세부사항을 처리
 */
@Component
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenDomainRepository {

    private final RefreshTokenRepository refreshTokenJpaRepository;

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        return refreshTokenJpaRepository.save(refreshToken);
    }

    @Override
    public Optional<RefreshToken> findActiveTokenByValue(String tokenValue) {
        return refreshTokenJpaRepository.findByTokenAndDeletedAtIsNull(tokenValue);
    }

    @Override
    public void deleteByUser(User user) {
        refreshTokenJpaRepository.deleteByUser(user);
    }

    @Override
    public void delete(RefreshToken refreshToken) {
        refreshTokenJpaRepository.delete(refreshToken);
    }
}