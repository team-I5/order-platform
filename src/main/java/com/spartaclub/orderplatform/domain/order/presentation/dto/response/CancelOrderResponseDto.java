package com.spartaclub.orderplatform.domain.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.spartaclub.orderplatform.domain.order.domain.model.OrderStatus;
import java.util.UUID;

@JsonInclude(Include.NON_NULL)
public record CancelOrderResponseDto(
    UUID orderId,
    OrderStatus status
) {

}
