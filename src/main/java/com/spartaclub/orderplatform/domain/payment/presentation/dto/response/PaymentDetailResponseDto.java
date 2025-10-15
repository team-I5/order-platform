package com.spartaclub.orderplatform.domain.payment.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentDetailResponseDto(
    UUID paymentId,        // 결제 ID
    UUID orderId,          // 주문 ID
    Long paymentAmount,    // 결제 금액
    String status,         // 결제 상태
    String pgPaymentKey,   // PG 결제 키
    String pgOrderId,      // PG 주문번호
    LocalDateTime createdAt // 생성일시
) {

}
