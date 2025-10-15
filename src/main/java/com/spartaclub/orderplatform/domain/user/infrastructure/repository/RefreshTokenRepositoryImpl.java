package com.spartaclub.orderplatform.domain.user.infrastructure.repository;

import com.spartaclub.orderplatform.domain.user.domain.entity.RefreshToken;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.domain.user.domain.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * RefreshTokenRepository 도메인 인터페이스의 구현체
 * JPA 기술을 사용하여 RefreshToken 도메인 데이터 접근 구현
 * Infrastructure 계층에서 도메인 계층의 추상화를 구체화
 *
 * @author 전우선
 * @date 2025-10-15
 */
@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        return refreshTokenJpaRepository.save(refreshToken);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenJpaRepository.findByToken(token);
    }

    @Override
    public void delete(RefreshToken refreshToken) {
        refreshTokenJpaRepository.delete(refreshToken);
    }

    @Override
    public Optional<RefreshToken> findByUser(User user) {
        return refreshTokenJpaRepository.findByUser(user);
    }

    @Override
    public Optional<RefreshToken> findByUserId(Long userId) {
        return refreshTokenJpaRepository.findByUserUserId(userId);
    }

    @Override
    public void deleteByUser(User user) {
        refreshTokenJpaRepository.deleteByUser(user);
    }

    @Override
    public void deleteByUserId(Long userId) {
        refreshTokenJpaRepository.deleteByUserUserId(userId);
    }

    @Override
    public void deleteByToken(String token) {
        refreshTokenJpaRepository.deleteByToken(token);
    }

    @Override
    public Optional<RefreshToken> findActiveByToken(String token) {
        // RefreshToken 엔티티에 deletedAt 필드가 있다면 활용, 없다면 findByToken과 동일
        return refreshTokenJpaRepository.findByToken(token);
    }
}