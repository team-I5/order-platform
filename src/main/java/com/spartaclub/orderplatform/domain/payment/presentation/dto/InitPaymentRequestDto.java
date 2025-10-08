package com.spartaclub.orderplatform.domain.payment.presentation.dto;

import java.util.UUID;

public record InitPaymentRequestDto(
    UUID orderId,
    String method,
    Long amount
) {

}
