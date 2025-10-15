package com.spartaclub.orderplatform.global.auth.exception;

import com.spartaclub.orderplatform.global.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AuthErrorCode implements ErrorCode {
    UNAUTHENTICATED(HttpStatus.UNAUTHORIZED, "Authentication required"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Access denied");

    private final HttpStatus status;
    private final String message;

    AuthErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.name();
    }
}
