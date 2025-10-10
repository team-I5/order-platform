package com.spartaclub.orderplatform.domain.payment.presentation.dto;

import java.util.List;

public record PaymentsListResponseDto(
    List<PaymentDetailResponseDto> payments,
    PageableDto pageable
) {

    public record PageableDto(
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious,
        boolean isFirst,
        boolean isLast
    ) {

    }
}
