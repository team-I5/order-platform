package com.spartaclub.orderplatform.global.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 설정 클래스
 * 개발 환경에서 백엔드 팀원들이 자유롭게 접근할 수 있도록 모든 보안 제약을 해제
 * 
 * @author 전우선
 * @date 2025-09-30(화)
 */
@Configuration // Spring 설정 클래스임을 나타내는 어노테이션
@EnableWebSecurity // Spring Security 웹 보안 기능을 활성화하는 어노테이션
public class SecurityConfig {

    /**
     * HTTP 보안 설정을 정의하는 SecurityFilterChain 빈 생성
     * @param http HttpSecurity 객체 - HTTP 보안 설정을 구성하는 빌더
     * @return SecurityFilterChain - 구성된 보안 필터 체인
     * @throws Exception 보안 설정 중 발생할 수 있는 예외
     */
    @Bean // Spring 컨테이너가 관리하는 빈으로 등록
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF(Cross-Site Request Forgery) 보호 기능을 비활성화
            // 개발 환경에서 API 테스트를 용이하게 하기 위해 해제
            .csrf(csrf -> csrf.disable())
            // HTTP 요청에 대한 인증/인가 규칙 설정
            .authorizeHttpRequests(auth -> auth
                // 모든 요청("/**")에 대해 인증 없이 접근 허용
                // 개발 초기 단계에서 백엔드 팀원들의 자유로운 접근을 위해 설정
                .anyRequest().permitAll()
            );
        
        // 설정된 보안 규칙을 바탕으로 SecurityFilterChain 객체 생성 및 반환
        return http.build();
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