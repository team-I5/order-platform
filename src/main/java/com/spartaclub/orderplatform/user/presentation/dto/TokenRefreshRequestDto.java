package com.spartaclub.orderplatform.user.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 토큰 갱신 요청 DTO 클라이언트에서 리프레시 토큰을 통해 액세스 토큰 갱신 요청 시 사용
 *
 * @author 전우선
 * @date 2025-10-03(금)
 */
@Getter
@Setter
@NoArgsConstructor
public class TokenRefreshRequestDto {

    @NotBlank(message = "리프레시 토큰은 필수입니다.")
    private String refreshToken;
}