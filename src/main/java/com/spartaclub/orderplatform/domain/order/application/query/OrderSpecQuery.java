package com.spartaclub.orderplatform.domain.order.application.query;

import com.spartaclub.orderplatform.domain.order.domain.model.OrderStatus;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import java.util.List;

public record OrderSpecQuery(
    List<OrderStatus> status,
    User viewer
) {

}
