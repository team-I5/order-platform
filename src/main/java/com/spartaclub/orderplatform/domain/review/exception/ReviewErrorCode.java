package com.spartaclub.orderplatform.domain.review.exception;

import com.spartaclub.orderplatform.global.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ReviewErrorCode implements ErrorCode {
    NOT_EXIST(HttpStatus.NOT_FOUND, "존재하지 않는 리뷰입니다."),
    ALREADY_EXIST_IN_REVIEW(HttpStatus.BAD_REQUEST, "리뷰가 존재하는 주문입니다.");

    private final HttpStatus status;
    private final String message;

    ReviewErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.name();
    }
}
