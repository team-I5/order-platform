package com.spartaclub.orderplatform.domain.order.exception;

import com.spartaclub.orderplatform.global.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum StoreRefErrorCode implements ErrorCode {
    NOT_EXIST(HttpStatus.NOT_FOUND, "Store Not Found"),
    ;

    private final HttpStatus status;
    private final String message;

    StoreRefErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.name();
    }
}
