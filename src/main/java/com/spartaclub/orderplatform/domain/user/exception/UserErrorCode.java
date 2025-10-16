package com.spartaclub.orderplatform.domain.user.exception;

import com.spartaclub.orderplatform.global.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserErrorCode implements ErrorCode {
    // 사용자 인증/인가 관련
    INVALID_LOGIN_CREDENTIALS(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 일치하지 않습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 리프레시 토큰입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    
    // 중복 데이터 관련
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "이미 존재하는 사용자명입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 존재하는 닉네임입니다."),
    DUPLICATE_PHONE_NUMBER(HttpStatus.CONFLICT, "이미 존재하는 전화번호입니다."),
    DUPLICATE_BUSINESS_NUMBER(HttpStatus.CONFLICT, "이미 존재하는 사업자번호입니다."),
    
    // 비밀번호 관련
    CURRENT_PASSWORD_REQUIRED(HttpStatus.BAD_REQUEST, "비밀번호 변경 시 현재 비밀번호는 필수입니다."),
    CURRENT_PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "현재 비밀번호가 일치하지 않습니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    
    // 권한 관련
    INSUFFICIENT_PERMISSION_FOR_BUSINESS_NUMBER(HttpStatus.FORBIDDEN, "CUSTOMER 권한으로는 사업자번호를 수정할 수 없습니다."),
    
    // 페이징 관련
    INVALID_PAGE_SIZE(HttpStatus.BAD_REQUEST, "페이지 크기는 1~50 사이여야 합니다.");

    private final HttpStatus status;
    private final String message;

    UserErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.name();
    }
}