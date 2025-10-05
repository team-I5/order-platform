package com.spartaclub.orderplatform.user.presentation.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 로그아웃 응답 DTO
 * 로그아웃 성공 시 클라이언트에게 반환되는 데이터 전송 객체
 *
 * @author 전우선
 * @date 2025-10-03(금)
 */
@Getter
@Setter
public class LogoutResponseDto {

    // 로그아웃 성공 메시지
    private String message;

    // 로그아웃 처리 시간
    private String timestamp;

    /**
     * 기본 생성자
     */
    public LogoutResponseDto() {
        // 기본 생성자는 비어있음
    }

    /**
     * 로그아웃 성공 응답 생성 정적 메서드
     *
     * @return 로그아웃 응답 DTO
     */
    public static LogoutResponseDto success() {
        LogoutResponseDto response = new LogoutResponseDto();
        response.message = "로그아웃되었습니다.";
        response.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        return response;
    }
}