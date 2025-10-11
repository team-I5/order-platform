package com.spartaclub.orderplatform.domain.order.domain.model;

public enum OrderStatus {
    PAYMENT_PENDING,   // 결제 대기중
    PAID,              // 결제완료
    CANCELED,          // 고객 취소
    DELIVERED,          // 배달완료
    ACCEPTED,          // 점주 승인
    REJECTED,          // 점주 거절
}
