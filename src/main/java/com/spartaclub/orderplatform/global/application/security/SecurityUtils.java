package com.spartaclub.orderplatform.global.application.security;

import com.spartaclub.orderplatform.global.auth.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * SecurityContext에서 현재 인증된 사용자 정보를 추출하는 유틸리티 클래스
 *
 * @author 류형선
 * @date 2025-10-11(토)
 */
@Slf4j
public class SecurityUtils {

    /**
     * 현재 인증된 사용자의 userId 반환
     *
     * @return 로그인된 사용자의 ID (없으면 null)
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            log.debug("인증 정보가 없거나 익명 사용자입니다.");
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetailsImpl userDetails) {
            return userDetails.getUser().getUserId();
        } else {
            log.warn("예상치 못한 principal 타입: {}", principal.getClass().getName());
            return null;
        }
    }
}
