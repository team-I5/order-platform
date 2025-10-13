package com.spartaclub.orderplatform.domain.order.domain.repository;

import com.spartaclub.orderplatform.domain.order.domain.model.Order;
import java.util.UUID;
import org.springframework.data.domain.Page;

public interface OrderRepository {

    void save(Order order);

    Order findById(UUID orderId);

    Page<Order> findAll(int page, int size);
}
