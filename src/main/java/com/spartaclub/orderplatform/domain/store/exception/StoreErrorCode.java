package com.spartaclub.orderplatform.domain.store.exception;

import com.spartaclub.orderplatform.global.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum StoreErrorCode implements ErrorCode {
    NOT_EXIST(HttpStatus.NOT_FOUND, "Store not found"),
    ;

    private final HttpStatus status;
    private final String message;

    StoreErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.name();
    }
}
