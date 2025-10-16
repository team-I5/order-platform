package com.spartaclub.orderplatform.domain.order.domain.model;

public enum OrderStatus {
    PAYMENT_PENDING,   // 결제 대기중
    PAID,              // 결제완료
    CANCELED,          // 고객 취소
    DELIVERED,          // 배달완료
    ACCEPTED,          // 점주 승인
    REJECTED,          // 점주 거절
    ;

    boolean isPending() {
        return this == PAYMENT_PENDING;
    }

    boolean isNotPending() {
        return !isPending();
    }

    boolean isPaid() {
        return this == PAID;
    }

    boolean isNotPaid() {
        return !isPaid();
    }

    boolean isCanceled() {
        return this == CANCELED;
    }

    boolean isNotCanceled() {
        return !isCanceled();
    }

    boolean isDelivered() {
        return this == DELIVERED;
    }

    boolean isNotDelivered() {
        return !isDelivered();
    }

    boolean isAccepted() {
        return this == ACCEPTED;
    }

    boolean isNotAccepted() {
        return !isAccepted();
    }

    boolean isRejected() {
        return this == REJECTED;
    }

    boolean isNotRejected() {
        return !isRejected();
    }
}
