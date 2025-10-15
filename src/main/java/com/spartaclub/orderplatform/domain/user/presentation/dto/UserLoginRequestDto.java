package com.spartaclub.orderplatform.domain.user.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 로그인 요청 DTO 클라이언트에서 전송하는 로그인 정보를 담는 데이터 전송 객체 이메일과 비밀번호에 대한 기본적인 유효성 검증 포함
 *
 * @author 전우선
 * @date 2025-10-02(목)
 */
@Getter
@Setter
@NoArgsConstructor
public class UserLoginRequestDto {

    // 로그인 이메일 (사용자명 역할)
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    // 로그인 비밀번호 (평문)
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
}