package com.spartaclub.orderplatform.user.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 회원 탈퇴 요청 DTO
 * 본인 확인을 위한 비밀번호 입력 데이터 전송 객체
 *
 * @author 전우선
 * @date 2025-10-04(토)
 */
@Getter
@Setter
@NoArgsConstructor
public class UserDeleteRequestDto {

    // 본인 확인용 현재 비밀번호
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
}