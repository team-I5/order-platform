package com.spartaclub.orderplatform.domain.payment.presentation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.UUID;

@JsonInclude(Include.NON_NULL) // null일 경우 JSON에 미포함
public record InitPaymentResponseDto(
    UUID paymentId,
    String redirectUrl,
    String PgPaymentKey,
    String PgOrderId                // PG사의 orderId, Order 도메인 엔티티의 PK가 아님
) {

}
