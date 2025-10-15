package com.spartaclub.orderplatform.domain.user.infrastructure.repository;

import com.spartaclub.orderplatform.domain.user.domain.entity.RefreshToken;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * RefreshToken JPA 레포지토리 인터페이스
 * Infrastructure 계층에서만 사용
 *
 * @author 전우선
 * @date 2025-10-15
 */
public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUser(User user);
    Optional<RefreshToken> findByUserUserId(Long userId);
    
    void deleteByUser(User user);
    void deleteByUserUserId(Long userId);
    void deleteByToken(String token);
}