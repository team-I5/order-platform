package com.spartaclub.orderplatform.domain.order.presentation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL) // null일 경우 JSON에 미포함
public record OrdersResponseDto(
    List<OrderDetailResponseDto> orders,
    PageableDto pageable
) {

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

