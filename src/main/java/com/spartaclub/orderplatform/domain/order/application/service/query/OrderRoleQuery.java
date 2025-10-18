package com.spartaclub.orderplatform.domain.order.application.service.query;

import com.spartaclub.orderplatform.domain.order.application.mapper.OrderMapper;
import com.spartaclub.orderplatform.domain.order.application.query.OrderSpecQuery;
import com.spartaclub.orderplatform.domain.order.domain.model.Order;
import com.spartaclub.orderplatform.domain.order.presentation.dto.request.GetOrdersRequestDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.response.OrderDetailResponseDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.response.OrdersResponseDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.response.OrdersResponseDto.OrderSummaryDto;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.domain.user.domain.entity.UserRole;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


//주문 조회 전략 인터페이스
public interface OrderRoleQuery {

    OrderMapper orderMapper = Mappers.getMapper(OrderMapper.class);

    UserRole supports(); // 전략이 담당하는 역할

    OrderDetailResponseDto getOrderDetail(UUID orderId, User viewer);

    OrdersResponseDto getOrders(GetOrdersRequestDto requestDto,
        User user,
        Pageable pageable);

    // 공통 구현: 다르면 오버라이드
    default OrdersResponseDto getOrdersCommon(
        GetOrdersRequestDto requestDto, User viewer, Pageable pageable,
        OrderReader orderReader, OrderMapper orderMapper
    ) {
        OrderSpecQuery orderSpecQuery = new OrderSpecQuery(requestDto.status(), viewer);
        Page<Order> orders = orderReader.findAll(orderSpecQuery, pageable);

        List<OrderSummaryDto> ordersList = orders.getContent().stream()
            .map(orderMapper::toSummaryDto)
            .collect(Collectors.toList());

        OrdersResponseDto.PageableDto meta = orderMapper.toPageableDto(orders);
        return new OrdersResponseDto(ordersList, meta);
    }
}
