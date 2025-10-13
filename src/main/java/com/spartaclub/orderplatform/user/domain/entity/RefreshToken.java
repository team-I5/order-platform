package com.spartaclub.orderplatform.user.domain.entity;

import com.spartaclub.orderplatform.global.domain.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 리프레시 토큰 엔티티 JWT 액세스 토큰 갱신을 위한 리프레시 토큰 정보 저장 RTR(Refresh Token Rotation) 패턴을 통한 보안 강화
 *
 * @author 전우선
 * @date 2025-10-02(목)
 */
@Entity
@Table(name = "p_refresh_token")
@Getter
@Setter
@NoArgsConstructor
public class RefreshToken extends BaseEntity {

    // 리프레시 토큰 고유 ID (기본키)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가
    @Column(name = "refreshTokenId")
    private Long refreshTokenId;

    // 실제 JWT 리프레시 토큰 문자열
    @Column(name = "token", nullable = false, length = 500)
    private String token;

    // 토큰 소유 사용자 (N:1 관계)
    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩으로 성능 최적화
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    // 토큰 만료 시간
    @Column(name = "expiresAt", nullable = false)
    private LocalDateTime expiresAt;

    /**
     * 리프레시 토큰 생성자 새로운 리프레시 토큰 생성 시 사용
     *
     * @param token     JWT 리프레시 토큰 문자열
     * @param user      토큰 소유 사용자
     * @param expiresAt 토큰 만료 시간
     */
    public RefreshToken(String token, User user, LocalDateTime expiresAt) {
        this.token = token;
        this.user = user;
        this.expiresAt = expiresAt;
    }

    /**
     * 토큰 만료 여부 확인 현재 시간과 만료 시간을 비교하여 만료 여부 판단
     *
     * @return 만료되었으면 true, 아니면 false
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }
}