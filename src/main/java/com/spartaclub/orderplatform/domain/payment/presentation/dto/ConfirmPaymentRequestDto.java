package com.spartaclub.orderplatform.domain.payment.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

public record ConfirmPaymentRequestDto(
    @NotNull(message = "주문 ID(orderId)는 필수입니다.")
    UUID orderId,

    @NotBlank(message = "결제 키(paymentKey)는 필수입니다.")
    String pgPaymentKey,

    @NotBlank(message = "PG사 orderId는 필수입니다.")
    String pgOrderId,

    @NotNull(message = "결제 금액은 필수입니다.")
    @Positive(message = "결제 금액은 0보다 커야 합니다.")
    Long amount
) {

}
