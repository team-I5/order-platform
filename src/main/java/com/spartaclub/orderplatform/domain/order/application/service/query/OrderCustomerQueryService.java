package com.spartaclub.orderplatform.domain.order.application.service.query;


import com.spartaclub.orderplatform.domain.order.domain.model.Order;
import com.spartaclub.orderplatform.domain.order.presentation.dto.request.GetOrdersRequestDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.response.OrderDetailResponseDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.response.OrdersResponseDto;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.domain.user.domain.entity.UserRole;
import com.spartaclub.orderplatform.global.auth.exception.AuthErrorCode;
import com.spartaclub.orderplatform.global.exception.BusinessException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class OrderCustomerQueryService implements OrderRoleQuery {

    private final OrderReader orderReader;

    @Override
    public UserRole supports() {
        return UserRole.CUSTOMER;
    }

    @Override
    public OrderDetailResponseDto getOrderDetail(UUID orderId, User viewer) {
        Order order = orderReader.findById(orderId);

        // 본인 주문만
        Long odererId = order.getUser().getUserId();
        Long viewerId = viewer.getUserId();
        if (!odererId.equals(viewerId)) {
            log.warn(
                "CUSTOMER는 본인의 주문만 조회할 수 있습니다. : role={}, viewerId={}, odererId={}, orderId={}",
                viewer.getRole(), viewerId, odererId, orderId);
            throw new BusinessException(AuthErrorCode.FORBIDDEN);
        }
        return orderMapper.toDto(order);
    }

    @Override
    public OrdersResponseDto getOrders(GetOrdersRequestDto requestDto, User viewer,
        Pageable pageable) {
        return getOrdersCommon(requestDto, viewer, pageable, orderReader, orderMapper);
    }
}
