package com.spartaclub.orderplatform.domain.order.domain.repository;

import com.spartaclub.orderplatform.domain.order.application.query.OrderQuery;
import com.spartaclub.orderplatform.domain.order.domain.model.Order;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepository {

    Order save(Order order);

    Page<Order> findAll(OrderQuery orderQuery, Pageable pageable);

    Optional<Order> findById(UUID orderId);
}
