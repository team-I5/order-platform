package com.spartaclub.orderplatform.domain.order.exception;

import com.spartaclub.orderplatform.global.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum OrderErrorCode implements ErrorCode {
    NOT_EXIST(HttpStatus.NOT_FOUND, "Order Not Found"),
    ;

    private final HttpStatus status;
    private final String message;

    OrderErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.name();
    }
}
