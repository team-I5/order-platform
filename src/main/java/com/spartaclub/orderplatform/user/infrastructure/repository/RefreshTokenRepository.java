package com.spartaclub.orderplatform.user.infrastructure.repository;

import com.spartaclub.orderplatform.user.domain.entity.RefreshToken;
import com.spartaclub.orderplatform.user.domain.entity.User;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 리프레시 토큰 레포지토리 인터페이스 RefreshToken 엔티티에 대한 데이터베이스 접근 기능 제공 RTR(Refresh Token Rotation) 패턴 지원을 위한 토큰
 * 관리 메서드 포함
 *
 * @author 전우선
 * @date 2025-10-03(금)
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * 토큰 문자열로 리프레시 토큰 조회 토큰 갱신 시 기존 토큰 검증에 사용
     *
     * @param token JWT 리프레시 토큰 문자열
     * @return 조회된 리프레시 토큰 엔티티 (Optional)
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * 토큰 문자열로 삭제되지 않은 리프레시 토큰 조회 소프트 삭제 패턴 적용하여 삭제되지 않은 토큰만 조회
     *
     * @param token JWT 리프레시 토큰 문자열
     * @return 조회된 리프레시 토큰 엔티티 (Optional)
     */
    Optional<RefreshToken> findByTokenAndDeletedAtIsNull(String token);

    /**
     * 사용자로 리프레시 토큰 조회 특정 사용자의 현재 리프레시 토큰 확인에 사용
     *
     * @param user 사용자 엔티티
     * @return 조회된 리프레시 토큰 엔티티 (Optional)
     */
    Optional<RefreshToken> findByUser(User user);

    /**
     * 특정 사용자의 모든 리프레시 토큰 삭제 로그아웃이나 새 토큰 발급 시 기존 토큰 무효화에 사용 RTR 패턴의 핵심 기능
     *
     * @param user 삭제 대상 사용자
     */
    @Modifying // 데이터 변경 쿼리임을 명시
    @Query("DELETE FROM RefreshToken rt WHERE rt.user = :user")
    void deleteByUser(@Param("user") User user);

    /**
     * 만료된 리프레시 토큰들을 일괄 삭제 스케줄러를 통한 주기적 정리 작업에 사용
     *
     * @param now 현재 시간 (이 시간보다 만료 시간이 이른 토큰들 삭제)
     */
    @Modifying // 데이터 변경 쿼리임을 명시
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);
}