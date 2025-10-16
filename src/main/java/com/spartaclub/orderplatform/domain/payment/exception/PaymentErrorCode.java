package com.spartaclub.orderplatform.domain.payment.exception;

import com.spartaclub.orderplatform.global.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum PaymentErrorCode implements ErrorCode {
    // 결제 공통
    NOT_EXIST(HttpStatus.NOT_FOUND, "Payment not found"),
    DUPLICATE_PAYMENT(HttpStatus.BAD_REQUEST, "이미 결제가 존재하는 주문입니다."),

    // 결제 상태 관련
    INVALID_STATUS_FOR_APPROVAL(HttpStatus.BAD_REQUEST, "결제 승인 가능한 상태가 아닙니다."),
    INVALID_STATUS_FOR_CANCEL(HttpStatus.BAD_REQUEST, "결제를 취소할 수 없는 상태입니다."),
    INVALID_PAYMENT_AMOUNT(HttpStatus.BAD_REQUEST, "결제 금액이 일치하지 않습니다."),

    // PG / 리다이렉트 관련
    INVALID_REDIRECT_URL_FORMAT(HttpStatus.BAD_REQUEST, "잘못된 Redirect URL 형식입니다."),
    REDIRECT_URL_PARSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Redirect URL 파싱 중 오류가 발생했습니다."),
    PAYMENT_GATEWAY_ERROR(HttpStatus.BAD_GATEWAY, "PG사 결제 처리 중 오류가 발생했습니다."),

    // PG 관련
    PG_ORDER_ID_MISMATCH(HttpStatus.BAD_REQUEST, "PG 주문번호가 일치하지 않습니다."),
    MISSING_PG_PAYMENT_KEY(HttpStatus.BAD_REQUEST, "PG 결제키가 존재하지 않습니다."),
    PG_PAYMENT_KEY_MISMATCH(HttpStatus.BAD_REQUEST, "PG 결제키가 일치하지 않습니다."),
    ;

    private final HttpStatus status;
    private final String message;

    PaymentErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.name();
    }
}
