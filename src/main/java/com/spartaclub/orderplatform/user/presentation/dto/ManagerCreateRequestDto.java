package com.spartaclub.orderplatform.user.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 관리자 계정 생성 요청 DTO
 * MASTER 권한 사용자가 MANAGER 계정을 생성할 때 사용
 *
 * @author 전우선
 * @date 2025-10-08(수)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagerCreateRequestDto {

    // 사용자명 (4-10자, 영소문자+숫자만)
    @NotBlank(message = "사용자명은 필수입니다.")
    @Size(min = 4, max = 10, message = "사용자명은 4-10자 이내여야 합니다.")
    @Pattern(regexp = "^[a-z0-9]+$", message = "사용자명은 영소문자와 숫자만 입력 가능합니다.")
    private String username;

    // 이메일 주소
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @Size(max = 100, message = "이메일은 100자 이내여야 합니다.")
    private String email;

    // 비밀번호 (8-15자, 영대소문자+숫자+특수문자 포함)
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 15, message = "비밀번호는 8-15자 이내여야 합니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "비밀번호는 영대소문자, 숫자, 특수문자를 모두 포함해야 합니다.")
    private String password;

    // 닉네임 (2-10자)
    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(min = 2, max = 10, message = "닉네임은 2-10자 이내여야 합니다.")
    private String nickname;

    // 연락처 (10-11자리 숫자)
    @NotBlank(message = "연락처는 필수입니다.")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "연락처는 10-11자리 숫자만 입력 가능합니다.")
    private String phoneNumber;
}