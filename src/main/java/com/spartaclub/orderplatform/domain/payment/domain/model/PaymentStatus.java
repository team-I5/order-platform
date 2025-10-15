package com.spartaclub.orderplatform.domain.payment.domain.model;

public enum PaymentStatus {
    PAYMENT_PENDING, // 결제 시도 중/미완료
    AUTHORIZED, // 승인만, 캡처 전 – 카드/해외 PG에 자주 있음
    CAPTURED, // 정식 매출 확정 = 결제완료
    CANCELED, // 승인 후 캡처 전 취소
    FAILED, // PG 오류/한도초과 등 실패
    REFUNDED, // 전액 환불
    ;

    boolean isPending() {
        return this == PAYMENT_PENDING;
    }

    boolean isNotPending() {
        return !isPending();
    }

    boolean isAuthorized() {
        return this == AUTHORIZED;
    }

    boolean isNotAuthorized() {
        return !isAuthorized();
    }

    boolean isCaptured() {
        return this == CAPTURED;
    }

    boolean isNotCaptured() {
        return !isCaptured();
    }

    boolean isFailed() {
        return this == FAILED;
    }

    boolean isNotFailed() {
        return !isFailed();
    }

    boolean isRefunded() {
        return this == REFUNDED;
    }

    boolean isNotRefunded() {
        return !isRefunded();
    }
}
