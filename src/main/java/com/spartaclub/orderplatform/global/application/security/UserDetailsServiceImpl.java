package com.spartaclub.orderplatform.global.application.security;

import com.spartaclub.orderplatform.user.domain.entity.User;
import com.spartaclub.orderplatform.user.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Security UserDetailsService 구현체
 * 사용자 인증을 위해 데이터베이스에서 사용자 정보를 조회하는 서비스
 * 이메일 또는 사용자 ID로 사용자 정보 로드 기능 제공
 *
 * @author 전우선
 * @date 2025-10-02(목)
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    // 사용자 정보 조회를 위한 리포지토리
    private final UserRepository userRepository;

    /**
     * 이메일로 사용자 정보 로드
     * Spring Security 인증 과정에서 호출되는 표준 메서드
     *
     * @param email 사용자 이메일 주소 (사용자명 역할)
     * @return UserDetails 구현체 (UserDetailsImpl)
     * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));

        return new UserDetailsImpl(user); // User 엔티티를 UserDetails로 래핑
    }

    /**
     * 사용자 ID로 사용자 정보 로드
     * JWT 토큰에서 추출한 사용자 ID로 사용자 정보를 조회할 때 사용
     * 실시간 권한 체크를 위해 매 요청마다 DB에서 최신 정보 조회
     *
     * @param userId 사용자 ID
     * @return UserDetails 구현체 (UserDetailsImpl)
     * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우
     */
    public UserDetails loadUserByUserId(Long userId) throws UsernameNotFoundException {
        User user = userRepository.findByUserIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        return new UserDetailsImpl(user); // User 엔티티를 UserDetails로 래핑
    }
}