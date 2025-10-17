package com.spartaclub.orderplatform.domain.order.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.spartaclub.orderplatform.domain.order.domain.model.OrderStatus;
import java.util.List;

public record GetOrdersRequestDto(
    @JsonAlias({"statuses", "status"})
    List<OrderStatus> status
) {

}
