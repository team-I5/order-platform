package com.spartaclub.orderplatform.domain.user.presentation.dto;

import com.spartaclub.orderplatform.domain.user.domain.entity.UserRole;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 회원가입 요청 DTO 클래스
 * 클라이언트로부터 받은 회원가입 데이터를 검증하고 전달
 *
 * @author 전우선
 * @date 2025-10-02(목)
 */
@Getter
@Setter
@NoArgsConstructor
public class UserSignupRequestDto {

    @NotBlank(message = "사용자명은 필수입니다.")
    @Size(min = 4, max = 10, message = "사용자명은 4-10자 이내여야 합니다.")
    @Pattern(regexp = "^[a-z0-9]+$", message = "사용자명은 영소문자와 숫자만 사용 가능합니다.")
    private String username;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @Size(max = 50, message = "이메일은 50자 이내여야 합니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 15, message = "비밀번호는 8-15자 이내여야 합니다.")
    // 영대소문자, 숫자, 특수문자 모두 포함 필수
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "비밀번호는 영대소문자, 숫자, 특수문자를 모두 포함해야 합니다.")
    private String password;

    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(min = 2, max = 10, message = "닉네임은 2-10자 이내여야 합니다.")
    private String nickname;

    @NotBlank(message = "연락처는 필수입니다.")
    @Pattern(regexp = "^\\d{10,11}$", message = "연락처는 10-11자리 숫자만 입력 가능합니다.")
    private String phoneNumber;

    @NotNull(message = "권한은 필수입니다.")
    private UserRole role;

    @Pattern(regexp = "^\\d{10}$", message = "사업자번호는 10자리 숫자만 입력 가능합니다.")
    private String businessNumber; // OWNER 권한 시 필수

    /**
     * 권한별 사업자번호 유효성 검증
     * OWNER 권한 선택 시 사업자번호 필수
     * CUSTOMER 권한 선택 시 사업자번호 입력 불가
     *
     * @return 유효성 검증 결과
     */
    @AssertTrue(message = "OWNER 권한 선택 시 사업자번호는 필수입니다.")
    public boolean isValidBusinessNumber() {
        if (role == UserRole.OWNER) {
            return businessNumber != null && !businessNumber.trim().isEmpty();
        } else if (role == UserRole.CUSTOMER) {
            return businessNumber == null || businessNumber.trim().isEmpty();
        }
        return true;
    }

    /**
     * 관리자 권한 선택 방지 검증
     * MANAGER, MASTER 권한은 일반 회원가입에서 선택 불가
     *
     * @return 유효성 검증 결과
     */
    @AssertTrue(message = "MANAGER, MASTER 권한은 선택할 수 없습니다.")
    public boolean isValidRole() {
        return role == UserRole.CUSTOMER || role == UserRole.OWNER;
    }
}