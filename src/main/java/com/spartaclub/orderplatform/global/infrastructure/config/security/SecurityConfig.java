package com.spartaclub.orderplatform.global.infrastructure.config.security;

import com.spartaclub.orderplatform.global.application.jwt.JwtAuthenticationFilter;
import com.spartaclub.orderplatform.global.application.jwt.JwtUtil;
import com.spartaclub.orderplatform.global.application.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 설정 클래스
 * JWT 기반 인증 및 권한 관리 설정
 * 실시간 권한 체크를 통한 보안 강화 구현
 *
 * @author 전우선
 * @date 2025-10-03(금)
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // JWT 토큰 처리를 위한 유틸리티 클래스
    private final JwtUtil jwtUtil;
    // 사용자 정보 로드를 위한 서비스
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * HTTP 보안 설정을 정의하는 SecurityFilterChain 빈 생성
     * JWT 기반 인증, 권한별 접근 제어, 실시간 권한 체크 설정
     *
     * @param http HttpSecurity 객체 - HTTP 보안 설정을 구성하는 빌더
     * @return SecurityFilterChain - 구성된 보안 필터 체인
     * @throws Exception 보안 설정 중 발생할 수 있는 예외
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF(Cross-Site Request Forgery) 보호 기능을 비활성화
                // JWT 토큰 기반 인증에서는 CSRF 보호가 불필요
                .csrf(csrf -> csrf.disable())

                // 세션 관리 정책을 STATELESS로 설정
                // JWT 토큰 기반 인증에서는 서버 세션을 사용하지 않음
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // HTTP 요청에 대한 인증/인가 규칙 설정
                .authorizeHttpRequests(auth -> auth
                        // 인증 없이 접근 가능한 엔드포인트 (회원가입, 로그인, 토큰 갱신)
                        .requestMatchers("/v1/users/signup", "/v1/users/login", "/v1/auth/refresh").permitAll()

                        // 관리자 계정 생성은 MASTER 권한만 접근 가능
                        .requestMatchers("/v1/users/manager").hasRole("MASTER")

                        // 사용자 목록 조회는 MANAGER, MASTER 권한만 접근 가능 (정확한 경로 지정)
                        .requestMatchers("/v1/users", "/v1/users/").hasAnyRole("MANAGER", "MASTER")

                        // 개별 사용자 관련 기능은 인증된 사용자 모두 접근 가능
                        .requestMatchers(HttpMethod.GET, "/v1/users/me").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/v1/users/me").authenticated()
                        .requestMatchers(HttpMethod.POST, "/v1/users/logout").authenticated()

                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )

                // JWT 인증 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
                // 모든 요청에서 JWT 토큰 검증 및 실시간 권한 체크 수행
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // 설정된 보안 규칙을 바탕으로 SecurityFilterChain 객체 생성 및 반환
        return http.build();
    }

    /**
     * JWT 인증 필터 빈 생성
     * 실시간 권한 체크를 위한 커스텀 JWT 필터
     *
     * @return JwtAuthenticationFilter 인스턴스
     */
    @Bean // Spring 컨테이너가 관리하는 빈으로 등록
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil, userDetailsService);
    }

    /**
     * BCryptPasswordEncoder 빈 생성
     * 비밀번호 암호화를 위한 BCrypt 해시 알고리즘 인코더 제공
     *
     * @return BCryptPasswordEncoder - 비밀번호 암호화 인코더
     */
    @Bean // Spring 컨테이너가 관리하는 빈으로 등록
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt 알고리즘을 사용하는 비밀번호 인코더 생성
    }
}