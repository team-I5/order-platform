package com.spartaclub.orderplatform.domain.order.infrastructure.repository;

import com.spartaclub.orderplatform.domain.order.application.query.OrderSpecQuery;
import com.spartaclub.orderplatform.domain.order.domain.model.Order;
import com.spartaclub.orderplatform.domain.order.domain.repository.OrderRepository;
import com.spartaclub.orderplatform.domain.order.infrastructure.repository.spec.OrderSpecs;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(order);
    }

    @Override
    public Page<Order> findAll(OrderSpecQuery orderSpecQuery, Pageable pageable) {
        Specification<Order> spec = (root, query, cb) -> cb.conjunction(); // 초기값
        spec = spec
            .and(OrderSpecs.visibleFor(orderSpecQuery.viewer()))
            .and(OrderSpecs.statusIn(orderSpecQuery.status()));
        return orderJpaRepository.findAll(spec, pageable);
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        return orderJpaRepository.findById(orderId);
    }
}
