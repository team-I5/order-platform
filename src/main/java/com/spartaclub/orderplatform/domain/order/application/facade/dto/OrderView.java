package com.spartaclub.orderplatform.domain.order.application.facade.dto;

import com.spartaclub.orderplatform.domain.order.domain.model.Order;
import java.util.UUID;

public record OrderView(
    UUID orderId,
    Long customerId,
    Long totalPrice,
    Integer productCount,
    String status
) {

    public static OrderView from(Order order) {
        return new OrderView(
            order.getOrderId(),
            order.getCreatedId(),
            order.getTotalPrice(),
            order.getProductCount(),
            order.getStatus().toString()
        );
    }
}
