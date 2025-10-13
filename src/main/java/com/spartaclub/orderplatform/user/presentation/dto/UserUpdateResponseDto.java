package com.spartaclub.orderplatform.user.presentation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 회원정보 수정 응답 DTO 회원정보 수정 성공 시 클라이언트에게 반환되는 데이터 전송 객체
 *
 * @author 전우선
 * @date 2025-10-03(금)
 */
@Getter
@Setter
@NoArgsConstructor
public class UserUpdateResponseDto {

    // 수정 성공 메시지
    private String message;

    // 수정된 사용자 정보
    private UserInfoDto userInfo;


}