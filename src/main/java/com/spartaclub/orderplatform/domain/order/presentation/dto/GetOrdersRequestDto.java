package com.spartaclub.orderplatform.domain.order.presentation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record GetOrdersRequestDto(
    @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다.")
    Integer page,

    @Min(value = 1, message = "최소 1개 이상 조회해야 합니다.")
    @Max(value = 100, message = "최대 100개까지만 조회할 수 있습니다.")
    Integer size,

    String sort
) {

    //기본값 지정
    public GetOrdersRequestDto {
        if (page == null) {
            page = 0;
        }
        if (size == null) {
            size = 20;
        }
        if (sort == null || sort.isBlank()) {
            sort = "createdAt,desc";
        }
    }
}
