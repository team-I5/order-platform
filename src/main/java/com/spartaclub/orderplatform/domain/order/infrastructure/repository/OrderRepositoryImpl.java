package com.spartaclub.orderplatform.domain.order.infrastructure.repository;

import com.spartaclub.orderplatform.domain.order.domain.model.Order;
import com.spartaclub.orderplatform.domain.order.domain.repository.OrderRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    public void save(Order order) {
        orderJpaRepository.save(order);
    }

    @Override
    public Order findById(UUID orderId) {
        return orderJpaRepository.findById(orderId).orElseThrow();
    }

    @Override
    public Page<Order> findAll(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return orderJpaRepository.findAll(pageRequest);
    }
}
