package com.spartaclub.orderplatform.domain.category.exception;

import com.spartaclub.orderplatform.global.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CategoryErrorCode implements ErrorCode {
    NOT_EXIST(HttpStatus.NOT_FOUND, "존재하지 않는 카테고리입니다."),
    ;

    private final HttpStatus status;
    private final String message;

    CategoryErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.name();
    }
}
