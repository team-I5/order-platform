package com.spartaclub.orderplatform.global.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.util.Base64;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * JWT 토큰 유틸리티 클래스 JWT 토큰 생성, 검증, 파싱 등의 기능을 제공 실시간 권한 체크를 위한 토큰 처리 로직 포함
 *
 * @author 전우선
 * @date 2025-10-02(목)
 */
@Slf4j
@Component
public class JwtUtil {

    // HTTP Authorization 헤더 이름
    public static final String AUTHORIZATION_HEADER = "Authorization";
    // Bearer 토큰 접두사
    public static final String BEARER_PREFIX = "Bearer ";

    // JWT 서명을 위한 비밀키 (application.yml에서 주입)
    @Value("${jwt.secret}")
    private String secretKey;

    // 액세스 토큰 만료 시간 (밀리초 단위, application.yml에서 주입)
    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    // 리프레시 토큰 만료 시간 (밀리초 단위, application.yml에서 주입)
    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    // JWT 서명에 사용할 SecretKey 객체
    private SecretKey key;

    /**
     * 빈 초기화 후 실행되는 메서드 Base64로 인코딩된 비밀키를 디코딩하여 SecretKey 객체 생성
     */
    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes); // HMAC-SHA 알고리즘용 키 생성
    }

    /**
     * 액세스 토큰 생성 사용자 인증 및 API 접근에 사용되는 JWT 토큰 생성
     *
     * @param userId 사용자 ID (토큰 subject로 사용)
     * @param email  사용자 이메일 (토큰 claim으로 포함)
     * @param role   사용자 권한 (실시간 권한 체크용)
     * @return 생성된 액세스 토큰 문자열
     */
    public String createAccessToken(Long userId, String email, String role) {
        Date now = new Date(); // 현재 시간
        Date expiration = new Date(now.getTime() + accessTokenExpiration); // 만료 시간 계산

        return Jwts.builder()
            .subject(String.valueOf(userId)) // 사용자 ID를 subject로 설정
            .claim("email", email) // 이메일 정보 포함
            .claim("role", role) // 권한 정보 포함 (실시간 권한 체크용)
            .claim("type", "access") // 토큰 타입 지정
            .issuedAt(now) // 발급 시간
            .expiration(expiration) // 만료 시간
            .signWith(key) // 비밀키로 서명
            .compact(); // 최종 토큰 문자열 생성
    }

    /**
     * 리프레시 토큰 생성 액세스 토큰 갱신에 사용되는 JWT 토큰 생성
     *
     * @param userId 사용자 ID
     * @return 생성된 리프레시 토큰 문자열
     */
    public String createRefreshToken(Long userId) {
        Date now = new Date(); // 현재 시간
        Date expiration = new Date(now.getTime() + refreshTokenExpiration); // 만료 시간 계산

        return Jwts.builder()
            .subject(String.valueOf(userId)) // 사용자 ID를 subject로 설정
            .claim("type", "refresh") // 토큰 타입 지정
            .issuedAt(now) // 발급 시간
            .expiration(expiration) // 만료 시간
            .signWith(key) // 비밀키로 서명
            .compact(); // 최종 토큰 문자열 생성
    }

    /**
     * Authorization 헤더에서 JWT 토큰 추출 "Bearer " 접두사를 제거하고 실제 토큰 문자열만 반환
     *
     * @param header Authorization 헤더 값
     * @return 추출된 JWT 토큰 문자열 (Bearer 접두사 제거)
     */
    public String getTokenFromHeader(String header) {
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length()); // "Bearer " 제거
        }
        return null; // 유효하지 않은 헤더인 경우 null 반환
    }

    /**
     * JWT 토큰에서 Claims 정보 추출 토큰을 파싱하여 포함된 모든 클레임 정보를 반환
     *
     * @param token JWT 토큰 문자열
     * @return 토큰에 포함된 Claims 객체
     * @throws ExpiredJwtException     토큰이 만료된 경우
     * @throws UnsupportedJwtException 지원되지 않는 토큰 형식인 경우
     * @throws MalformedJwtException   잘못된 형식의 토큰인 경우
     * @throws SecurityException       서명 검증에 실패한 경우
     */
    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                .verifyWith(key) // 비밀키로 서명 검증
                .build()
                .parseSignedClaims(token) // 서명된 토큰 파싱
                .getPayload(); // 페이로드(Claims) 반환
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰입니다.");
            throw e;
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 JWT 토큰입니다.");
            throw e;
        } catch (MalformedJwtException e) {
            log.warn("잘못된 형식의 JWT 토큰입니다.");
            throw e;
        } catch (SecurityException | IllegalArgumentException e) {
            log.warn("잘못된 JWT 토큰입니다.");
            throw e;
        }
    }

    /**
     * JWT 토큰에서 사용자 ID 추출
     *
     * @param token JWT 토큰 문자열
     * @return 토큰에 포함된 사용자 ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return Long.parseLong(claims.getSubject()); // subject에서 사용자 ID 추출
    }

    /**
     * JWT 토큰에서 이메일 정보 추출
     *
     * @param token JWT 토큰 문자열
     * @return 토큰에 포함된 이메일 주소
     */
    public String getEmailFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("email", String.class); // email 클레임에서 이메일 추출
    }

    /**
     * JWT 토큰에서 권한 정보 추출 실시간 권한 체크에 사용
     *
     * @param token JWT 토큰 문자열
     * @return 토큰에 포함된 사용자 권한
     */
    public String getRoleFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("role", String.class); // role 클레임에서 권한 추출
    }

    /**
     * JWT 토큰에서 토큰 타입 추출 access/refresh 토큰 구분에 사용
     *
     * @param token JWT 토큰 문자열
     * @return 토큰 타입 (access 또는 refresh)
     */
    public String getTokenTypeFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("type", String.class); // type 클레임에서 토큰 타입 추출
    }

    /**
     * JWT 토큰 만료 여부 확인
     *
     * @param token JWT 토큰 문자열
     * @return 토큰이 만료되었으면 true, 아니면 false
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getExpiration().before(new Date()); // 만료 시간과 현재 시간 비교
        } catch (ExpiredJwtException e) {
            return true; // 만료된 토큰인 경우 true 반환
        }
    }

    /**
     * JWT 토큰 유효성 검증 토큰의 서명, 형식, 만료 등을 종합적으로 검증
     *
     * @param token JWT 토큰 문자열
     * @return 토큰이 유효하면 true, 아니면 false
     */
    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token); // 토큰 파싱 시도
            return true; // 파싱 성공 시 유효한 토큰
        } catch (JwtException | IllegalArgumentException e) {
            return false; // 파싱 실패 시 유효하지 않은 토큰
        }
    }

    /**
     * 액세스 토큰인지 확인
     *
     * @param token JWT 토큰 문자열
     * @return 액세스 토큰이면 true, 아니면 false
     */
    public boolean isAccessToken(String token) {
        return "access".equals(getTokenTypeFromToken(token));
    }

    /**
     * 리프레시 토큰인지 확인
     *
     * @param token JWT 토큰 문자열
     * @return 리프레시 토큰이면 true, 아니면 false
     */
    public boolean isRefreshToken(String token) {
        return "refresh".equals(getTokenTypeFromToken(token));
    }

    /**
     * 액세스 토큰 만료 시간 반환 (초 단위) API 응답에서 expiresIn 필드로 사용
     *
     * @return 액세스 토큰 만료 시간 (초)
     */
    public long getAccessTokenExpirationInSeconds() {
        return accessTokenExpiration / 1000; // 밀리초를 초로 변환
    }

    /**
     * 리프레시 토큰 만료 시간 반환 (밀리초 단위) 리프레시 토큰 엔티티 생성 시 사용
     *
     * @return 리프레시 토큰 만료 시간 (밀리초)
     */
    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }

}