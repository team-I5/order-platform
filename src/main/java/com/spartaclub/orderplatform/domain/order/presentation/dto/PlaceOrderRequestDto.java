package com.spartaclub.orderplatform.domain.order.presentation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public record PlaceOrderRequestDto(
    @NotNull(message = "storeId는 필수 값입니다.")
    UUID storeId,

    @NotBlank(message = "address는 필수 값입니다.")
    String address,

    @NotEmpty(message = "주문 상품 목록은 최소 1개 이상이어야 합니다.")
    List<OrderItemRequest> items,

    @Size(max = 100, message = "메모는 최대 100자까지 가능합니다.")
    String memo
) {

    public record OrderItemRequest(
        @NotBlank(message = "productId는 필수 값입니다.")
        UUID productId,

        @Min(value = 1, message = "quantity는 1 이상이어야 합니다.")
        Integer quantity
    ) {

    }
}
