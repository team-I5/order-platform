package com.spartaclub.orderplatform.domain.payment.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CancelPaymentRequestDto(
    @NotBlank(message = "결제키(pgPaymentKey)는 필수입니다.")
    String pgPaymentKey,
    @NotBlank(message = "결제 취소 이유는 필수입니다.")
    String cancelReason
) {

}
