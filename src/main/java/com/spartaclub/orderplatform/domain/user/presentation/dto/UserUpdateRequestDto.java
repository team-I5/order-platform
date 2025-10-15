package com.spartaclub.orderplatform.domain.user.presentation.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 회원정보 수정 요청 DTO
 * 선택적 필드 수정을 위한 데이터 전송 객체
 * 제공된 필드만 수정하고, null인 필드는 기존값 유지
 *
 * @author 전우선
 * @date 2025-10-03(금)
 */
@Getter
@Setter
@NoArgsConstructor
public class UserUpdateRequestDto {

    // 사용자명 (선택적 수정)
    @Size(min = 4, max = 10, message = "사용자명은 4-10자 이내여야 합니다.")
    @Pattern(regexp = "^[a-z0-9]+$", message = "사용자명은 영소문자와 숫자만 사용 가능합니다.")
    private String username;

    // 닉네임 (선택적 수정)
    @Size(min = 2, max = 10, message = "닉네임은 2-10자 이내여야 합니다.")
    private String nickname;

    // 연락처 (선택적 수정)
    @Pattern(regexp = "^\\d{10,11}$", message = "연락처는 10-11자리 숫자만 입력 가능합니다.")
    private String phoneNumber;

    // 사업자번호 (선택적 수정, OWNER만 가능)
    @Pattern(regexp = "^\\d{10}$", message = "사업자번호는 10자리 숫자만 입력 가능합니다.")
    private String businessNumber;

    // 현재 비밀번호 (비밀번호 변경 시 필수)
    private String currentPassword;

    // 새 비밀번호 (선택적 수정)
    @Size(min = 8, max = 15, message = "비밀번호는 8-15자 이내여야 합니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "비밀번호는 영대소문자, 숫자, 특수문자를 모두 포함해야 합니다.")
    private String newPassword;

    /**
     * 비밀번호 변경 요청 여부 확인
     *
     * @return 새 비밀번호가 제공된 경우 true
     */
    public boolean isPasswordChangeRequested() {
        return newPassword != null && !newPassword.trim().isEmpty();
    }

    /**
     * 현재 비밀번호 제공 여부 확인
     *
     * @return 현재 비밀번호가 제공된 경우 true
     */
    public boolean isCurrentPasswordProvided() {
        return currentPassword != null && !currentPassword.trim().isEmpty();
    }
}