package com.spartaclub.orderplatform.domain.order.application.facade;

import com.spartaclub.orderplatform.domain.order.application.facade.dto.OrderView;
import com.spartaclub.orderplatform.domain.order.domain.repository.OrderRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderPublicReaderImpl implements OrderPublicReader {

    private final OrderRepository orderRepository;

    @Override
    public Optional<OrderView> loadOrderSummaryInfo(UUID orderId) {
        return orderRepository.findById(orderId).map(OrderView::from);
    }
}
