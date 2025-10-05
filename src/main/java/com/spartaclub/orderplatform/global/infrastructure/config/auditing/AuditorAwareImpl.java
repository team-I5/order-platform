package com.spartaclub.orderplatform.global.infrastructure.config.auditing;

import lombok.NonNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;


/**
 * 엔티티의 @CreatedBy, @LastModifiedBy 같은 작성자/수정자 정보를 채워주기 위해 현재 사용자의 ID를 알려주는 것.
 * 엔티티에서 @CreatedBy, @LastModifiedBy 붙은 필드가 있음.
 * 엔티티가 저장/수정될 때 JPA Auditing이 동작 → "지금 사용자 누구야?"라고 물어봄.
 * Spring은 AuditorAwareImpl.getCurrentAuditor()를 호출해서 값을 가져옴.
 * 해당 ID 값이 엔티티 필드에 자동으로 들어감.
 *
 * @author 류형선
 * @date 2025-09-30(화)
 *
 **/
@Component("auditorProvider")
public class AuditorAwareImpl implements AuditorAware<Long> {

    @Override
    @NonNull
    public Optional<Long> getCurrentAuditor() {
        // 여기에 현재 로그인한 사용자 ID를 반환
        // 보안 적용 전에는 테스트용으로 고정값 반환
        // 예: SecurityContextHolder에서 가져오기
        // Long userId = getCurrentUserIdFromSecurityContext();
        return Optional.of(0L); // 예: 0L = 시스템/익명
    }

//    private Long getCurrentUserIdFromSecurityContext() {
//        // 실제 구현: Spring Security 사용 시
//        // Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        // CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
//        // return user.getId();
//        return 1L; // 예시: 테스트용
//    }
}
