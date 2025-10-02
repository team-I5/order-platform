package com.spartaclub.orderplatform.domain.order.presentation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.spartaclub.orderplatform.domain.order.domain.model.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL) // null일 경우 JSON에 미포함
public record OrderDetailResponseDto(
    UUID orderId,
    Long userId,
    UUID storeId,
    Long totalPrice,
    Integer productCount,
    String address,
    OrderStatus status,
    String memo,
    LocalDateTime createdAt,
    LocalDateTime modifiedAt,
    LocalDateTime deletedAt,
    List<ProductsListItem> productsList,
    UUID createdId,
    UUID modifiedId,
    UUID deletedId
) {

    public record ProductsListItem(
        UUID productId,
        String name,
        Long price,
        Integer quantity,
        Long totalPrice
    ) {

    }
}
