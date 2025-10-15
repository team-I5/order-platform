package com.spartaclub.orderplatform.domain.order.application.dto.query;

import com.spartaclub.orderplatform.domain.order.domain.model.OrderStatus;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import java.util.List;

public record OrderQuery(
    List<OrderStatus> status,
    User viewer
) {

}
