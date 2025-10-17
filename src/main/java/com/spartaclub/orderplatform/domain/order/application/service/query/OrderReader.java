package com.spartaclub.orderplatform.domain.order.application.service.query;

import com.spartaclub.orderplatform.domain.order.application.query.OrderSpecQuery;
import com.spartaclub.orderplatform.domain.order.domain.model.Order;
import com.spartaclub.orderplatform.domain.order.domain.repository.OrderRepository;
import com.spartaclub.orderplatform.domain.order.exception.OrderErrorCode;
import com.spartaclub.orderplatform.global.exception.BusinessException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
class OrderReader {

    private final OrderRepository orderRepository;

    Order findById(UUID orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> {
                log.warn("[OrderReader] NOT_EXIST - orderId={}", orderId);
                return new BusinessException(OrderErrorCode.NOT_EXIST);
            });
    }

    Page<Order> findAll(OrderSpecQuery orderSpecQuery, Pageable pageable) {
        return orderRepository.findAll(orderSpecQuery, pageable);
    }

}
