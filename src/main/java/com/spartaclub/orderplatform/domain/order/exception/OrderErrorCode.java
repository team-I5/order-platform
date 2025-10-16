package com.spartaclub.orderplatform.domain.order.exception;

import com.spartaclub.orderplatform.global.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum OrderErrorCode implements ErrorCode {
    // 주문 공통
    NOT_EXIST(HttpStatus.NOT_FOUND, "Order Not Found"),

    // 결제 관련
    INVALID_STATUS_FOR_PAYMENT(HttpStatus.BAD_REQUEST, "결제를 진행할 수 없는 주문 상태입니다."),
    PAYMENT_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "결제 요청 금액이 주문 총액과 일치하지 않습니다."),

    // 주문 취소 관련
    INVALID_STATUS_FOR_CANCELLATION(HttpStatus.BAD_REQUEST, "해당 주문 상태에서는 취소할 수 없습니다."),
    INVALID_CREATED_TIME(HttpStatus.INTERNAL_SERVER_ERROR, "주문 생성 시각이 유효하지 않아 취소 검증을 수행할 수 없습니다."),
    CANCELLATION_WINDOW_EXPIRED(HttpStatus.BAD_REQUEST, "주문 후 5분이 지나 취소할 수 없습니다."),

    // 주문 승인
    INVALID_STATUS_FOR_ACCEPT(HttpStatus.BAD_REQUEST, "결제 완료 상태의 주문만 승인할 수 있습니다."),
    // 주문 거절
    INVALID_STATUS_FOR_REJECT(HttpStatus.BAD_REQUEST, "결제 대기 또는 결제 완료 상태의 주문만 거절할 수 있습니다."),

    // 주문 배달
    INVALID_STATUS_FOR_COMPLETE_DELIVERY(HttpStatus.BAD_REQUEST, "승인 상태의 주문만 배달 완료할 수 있습니다."),
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
