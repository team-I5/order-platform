package com.spartaclub.orderplatform.user.presentation.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.Getter;
import lombok.Setter;

/**
 * 로그아웃 응답 DTO 로그아웃 성공 시 클라이언트에게 반환되는 데이터 전송 객체
 *
 * @author 전우선
 * @date 2025-10-05(일)
 */
@Getter
@Setter
public class LogoutResponseDto {

    // 로그아웃 성공 메시지
    private String message;

    // 로그아웃 처리 시간
    private String timestamp;

    /**
     * 로그아웃 응답 DTO 생성자
     */
    public LogoutResponseDto() {
        this.message = "로그아웃되었습니다.";
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}