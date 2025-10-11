package com.spartaclub.orderplatform.domain.order.presentation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.spartaclub.orderplatform.domain.order.domain.model.OrderStatus;
import java.util.UUID;

@JsonInclude(Include.NON_NULL)
public record OrderStatusResponseDto(
    UUID orderId,
    OrderStatus status
) {

    public static OrderStatusResponseDto ofCanceled(UUID orderId) {
        return new OrderStatusResponseDto(orderId, OrderStatus.CANCELED);
    }

    public static OrderStatusResponseDto ofAccepted(UUID orderId) {
        return new OrderStatusResponseDto(orderId, OrderStatus.ACCEPTED);
    }

    public static OrderStatusResponseDto ofRejected(UUID orderId) {
        return new OrderStatusResponseDto(orderId, OrderStatus.REJECTED);
    }
}
