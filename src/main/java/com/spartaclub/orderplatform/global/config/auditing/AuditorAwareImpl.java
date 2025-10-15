package com.spartaclub.orderplatform.global.infrastructure.config.auditing;

import lombok.NonNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.spartaclub.orderplatform.global.application.security.SecurityUtils.getCurrentUserId;


/**
 * 엔티티의 @CreatedBy, @LastModifiedBy 같은 작성자/수정자 정보를 채워주기 위해 현재 사용자의 ID를 알려주는 것.
 * 엔티티에서 @CreatedBy, @LastModifiedBy 붙은 필드가 있음.
 * 엔티티가 저장/수정될 때 JPA Auditing이 동작 → "지금 사용자 누구야?"라고 물어봄.
 * Spring은 AuditorAwareImpl.getCurrentAuditor()를 호출해서 값을 가져옴.
 * 해당 ID 값이 엔티티 필드에 자동으로 들어감.
 *
 * @author 류형선
 * @date 2025-10-11(토)
 *
 **/
@Component("auditorProvider")
public class AuditorAwareImpl implements AuditorAware<Long> {

    @Override
    @NonNull
    public Optional<Long> getCurrentAuditor() {
        Long userId = getCurrentUserId();
        return Optional.ofNullable(userId);
    }

}
