package com.spartaclub.orderplatform.domain.payment.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

public record InitPaymentRequestDto(
    @NotNull(message = "주문 ID는 필수입니다.")
    UUID orderId,

    @NotBlank(message = "결제 수단은 필수입니다.")
    String method,

    @NotNull(message = "결제 금액은 필수입니다.")
    @Positive(message = "결제 금액은 0보다 커야 합니다.")
    Long amount
) {

}
