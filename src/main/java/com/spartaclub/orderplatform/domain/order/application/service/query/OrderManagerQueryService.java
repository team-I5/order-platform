package com.spartaclub.orderplatform.domain.order.application.service.query;

import com.spartaclub.orderplatform.domain.order.presentation.dto.request.GetOrdersRequestDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.response.OrderDetailResponseDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.response.OrdersResponseDto;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.domain.user.domain.entity.UserRole;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderManagerQueryService implements OrderRoleQuery {

    private final OrderReader orderReader;

    @Override
    public UserRole supports() {
        return UserRole.MANAGER;
    }

    @Override
    public OrderDetailResponseDto getOrderDetail(UUID orderId, User viewer) {
        return orderMapper.toDto(orderReader.findById(orderId));
    }

    @Override
    public OrdersResponseDto getOrders(GetOrdersRequestDto requestDto, User viewer,
        Pageable pageable) {
        return getOrdersCommon(requestDto, viewer, pageable, orderReader, orderMapper);
    }

}
