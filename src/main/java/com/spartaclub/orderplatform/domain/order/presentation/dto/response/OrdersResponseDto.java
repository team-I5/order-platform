package com.spartaclub.orderplatform.domain.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.spartaclub.orderplatform.domain.order.domain.model.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL) // null일 경우 JSON에 미포함
public record OrdersResponseDto(
    List<OrderSummaryDto> orders,
    PageableDto pageable
) {

    public record OrderSummaryDto(
        UUID orderId,
        Long userId,
        UUID storeId,
        Long totalPrice,
        Integer productCount,
        String address,
        OrderStatus status,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt,
        LocalDateTime deletedAt
    ) {

    }

    public record PageableDto(
        Integer page,
        Integer size,
        Integer totalElements,
        Integer totalPages,
        Boolean hasNext,
        Boolean hasPrevious,
        Boolean isFirst,
        Boolean isLast
    ) {

    }
}

