package com.spartaclub.orderplatform.domain.order.application.service.query;

import com.spartaclub.orderplatform.domain.order.presentation.dto.request.GetOrdersRequestDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.response.OrderDetailResponseDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.response.OrdersResponseDto;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.domain.user.domain.entity.UserRole;
import com.spartaclub.orderplatform.global.auth.exception.AuthErrorCode;
import com.spartaclub.orderplatform.global.exception.BusinessException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OrderQueryFacade {

    private final Map<UserRole, OrderRoleQuery> orderRoleQueryMap;

    public OrderQueryFacade(List<OrderRoleQuery> orderRoleQueries) {
        // 전달받은 List를 Map<UserRole, OrderRoleQuery>으로 변환
        orderRoleQueryMap = orderRoleQueries.stream()
            .collect(Collectors.toUnmodifiableMap(OrderRoleQuery::supports, it -> it));
    }

    public OrderDetailResponseDto getOrderDetail(UUID orderId, User viewer) {
        OrderRoleQuery strategy = orderRoleQueryMap.get(viewer.getRole());
        if (strategy == null) {
            throw new BusinessException(AuthErrorCode.FORBIDDEN);
        }
        return strategy.getOrderDetail(orderId, viewer);
    }

    public OrdersResponseDto getOrders(
        GetOrdersRequestDto requestDto,
        User viewer,
        Pageable pageable) {

        OrderRoleQuery strategy = orderRoleQueryMap.get(viewer.getRole());
        if (strategy == null) {
            throw new BusinessException(AuthErrorCode.FORBIDDEN);
        }
        return strategy.getOrders(requestDto, viewer, pageable);
    }

}
