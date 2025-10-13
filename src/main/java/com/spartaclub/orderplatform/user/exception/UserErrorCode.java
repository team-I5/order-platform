package com.spartaclub.orderplatform.user.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserErrorCode {
    NOT_EXIST(HttpStatus.NOT_FOUND, "존재하지 않는 사용자 입니다."),
    DUPLICATED_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
    DUPLICATED_USERNAME(HttpStatus.CONFLICT, "이미 존재하는 사용자명입니다."),
    DUPLICATED_PHONE_NUMBER(HttpStatus.CONFLICT, "이미 존재하는 전화번호입니다.");

    private final HttpStatus status;
    private final String message;

    UserErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
