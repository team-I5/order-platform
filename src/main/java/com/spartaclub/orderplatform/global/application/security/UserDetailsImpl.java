package com.spartaclub.orderplatform.global.application.security;

import com.spartaclub.orderplatform.user.domain.entity.User;
import com.spartaclub.orderplatform.user.domain.entity.UserRole;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Spring Security UserDetails 구현체
 * User 엔티티를 Spring Security에서 사용할 수 있도록 어댑터 역할
 * 인증 및 권한 처리에 필요한 사용자 정보 제공
 *
 * @author 전우선
 * @date 2025-10-02(목)
 */
@Getter
@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {

    // 실제 사용자 엔티티 객체 (@Getter로 getUser() 메서드 자동 생성)
    private final User user;

    /**
     * 사용자 비밀번호 반환
     * Spring Security 인증 과정에서 비밀번호 검증에 사용
     *
     * @return 암호화된 비밀번호
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * 사용자명 반환
     * 이메일을 사용자명으로 사용 (로그인 ID)
     *
     * @return 사용자 이메일 주소
     */
    @Override
    public String getUsername() {
        return user.getEmail(); // 이메일을 사용자명으로 사용
    }

    /**
     * 사용자 권한 목록 반환
     * Spring Security 권한 체크에 사용
     *
     * @return 사용자 권한 목록 (ROLE_ 접두사 포함)
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        UserRole role = user.getRole();
        String authority = "ROLE_" + role.name(); // ROLE_CUSTOMER, ROLE_OWNER 등

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(authority));
        return authorities;
    }

    /**
     * 계정 활성화 상태 확인
     * 탈퇴하지 않은 사용자만 활성 상태로 판단
     *
     * @return 활성 상태면 true, 비활성 상태면 false
     */
    @Override
    public boolean isEnabled() {
        return user.getDeletedAt() == null; // 탈퇴하지 않은 사용자만 활성화
    }

    /**
     * 계정 만료 여부 확인
     * 현재 프로젝트에서는 계정 만료 기능을 사용하지 않음
     *
     * @return 항상 true (계정 만료 없음)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 계정 잠금 여부 확인
     * 현재 프로젝트에서는 계정 잠금 기능을 사용하지 않음
     *
     * @return 항상 true (계정 잠금 없음)
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 자격 증명 만료 여부 확인
     * 현재 프로젝트에서는 자격 증명 만료 기능을 사용하지 않음
     *
     * @return 항상 true (자격 증명 만료 없음)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}