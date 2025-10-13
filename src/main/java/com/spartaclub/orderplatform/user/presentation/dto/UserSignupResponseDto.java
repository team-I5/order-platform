package com.spartaclub.orderplatform.user.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 회원가입 응답 DTO 클래스 회원가입 성공 시 클라이언트에게 반환할 데이터
 *
 * @author 전우선
 * @date 2025-10-05(일)
 */
@Getter
@AllArgsConstructor
public class UserSignupResponseDto {

    private String message;
    private Long userId;

}