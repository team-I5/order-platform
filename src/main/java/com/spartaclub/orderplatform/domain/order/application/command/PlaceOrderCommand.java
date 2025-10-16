package com.spartaclub.orderplatform.domain.order.application.command;

import java.util.UUID;

public record PlaceOrderCommand(
    UUID productId,
    Integer quantity
) {

}
