package com.spartaclub.orderplatform.domain.order.domain.model;

public enum OrderStatus {
    PAYMENT_PENDING,   // 결제중
    PAID,              // 결제완료
    CANCELED,          // 결제취소
    DELIVERING,        // 배달중
    DELIVERED          // 배달완료
}
