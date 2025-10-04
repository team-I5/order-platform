package com.spartaclub.orderplatform.global.application.jwt;

import com.spartaclub.orderplatform.global.application.security.UserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 인증 필터
 * 모든 HTTP 요청에서 JWT 토큰을 검증하고 Spring Security 인증 컨텍스트를 설정
 * 실시간 권한 체크를 위해 매 요청마다 DB에서 최신 사용자 정보 조회
 *
 * @author 전우선
 * @date 2025-10-04(토)
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // JWT 토큰 처리를 위한 유틸리티 클래스
    private final JwtUtil jwtUtil;
    // 사용자 정보 로드를 위한 서비스
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * 모든 HTTP 요청에서 실행되는 필터 메서드
     * JWT 토큰 검증 및 Spring Security 인증 컨텍스트 설정
     * 실시간 권한 체크를 통한 보안 강화
     *
     * @param request     HTTP 요청 객체
     * @param response    HTTP 응답 객체
     * @param filterChain 필터 체인
     * @throws ServletException 서블릿 예외
     * @throws IOException      입출력 예외
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 요청 헤더에서 JWT 토큰 추출
        String token = resolveToken(request);

        // 2. 토큰이 존재하는 경우 검증 및 인증 처리
        if (StringUtils.hasText(token)) {
            try {
                // 3. 토큰 유효성 검증 및 액세스 토큰인지 확인
                if (jwtUtil.validateToken(token) && jwtUtil.isAccessToken(token)) {
                    Long userId = jwtUtil.getUserIdFromToken(token);

                    // 4. 실시간 권한 체크: DB에서 최신 사용자 정보 조회
                    // 권한 변경이나 계정 탈퇴 등의 실시간 반영을 위해 매번 DB 조회
                    UserDetails userDetails;
                    try {
                        userDetails = userDetailsService.loadUserByUserId(userId);
                    } catch (Exception e) {
                        // 사용자가 탈퇴했거나 존재하지 않는 경우 접근 차단
                        log.warn("사용자 정보 조회 실패. 사용자 ID: {}", userId);
                        filterChain.doFilter(request, response);
                        return;
                    }

                    // 5. 토큰의 권한과 현재 DB의 권한 비교
                    String tokenRole = jwtUtil.getRoleFromToken(token);
                    String currentRole = userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");

                    // 6. 권한이 변경된 경우 접근 차단
                    if (!tokenRole.equals(currentRole)) {
                        log.warn("토큰의 권한({})과 현재 권한({})이 다릅니다. 사용자 ID: {}", tokenRole, currentRole, userId);
                        filterChain.doFilter(request, response);
                        return;
                    }

                    // 7. 사용자가 활성 상태인지 확인 (탈퇴 여부)
                    if (!userDetails.isEnabled()) {
                        log.warn("비활성 사용자의 토큰 접근 시도. 사용자 ID: {}", userId);
                        filterChain.doFilter(request, response);
                        return;
                    }

                    // 8. Spring Security 인증 객체 생성
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );

                    // 9. Security Context에 인증 정보 설정
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(authentication);
                    SecurityContextHolder.setContext(context);
                } else {
                    log.warn("유효하지 않은 JWT 토큰입니다.");
                }
            } catch (ExpiredJwtException e) {
                log.warn("만료된 JWT 토큰입니다.");
            } catch (Exception e) {
                log.error("JWT 토큰 처리 중 오류가 발생했습니다.", e);
            }
        }

        // 10. 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    /**
     * HTTP 요청에서 JWT 토큰 추출
     * Authorization 헤더에서 Bearer 토큰을 찾아 반환
     *
     * @param request HTTP 요청 객체
     * @return 추출된 JWT 토큰 문자열 (Bearer 접두사 제거됨)
     */
    private String resolveToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(JwtUtil.AUTHORIZATION_HEADER);
        return jwtUtil.getTokenFromHeader(authorizationHeader); // "Bearer " 접두사 제거 후 반환
    }
}