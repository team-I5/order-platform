package com.spartaclub.orderplatform.domain.order.application.service.query;

import com.spartaclub.orderplatform.domain.order.application.dto.StoreInfoDto;
import com.spartaclub.orderplatform.domain.order.application.query.StoreQuery;
import com.spartaclub.orderplatform.domain.order.domain.model.Order;
import com.spartaclub.orderplatform.domain.order.domain.repository.StoreReaderRepository;
import com.spartaclub.orderplatform.domain.order.exception.StoreRefErrorCode;
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
public class OrderOwnerQueryService implements OrderRoleQuery {

    private final OrderReader orderReader;
    private final StoreReaderRepository storeReaderRepository;

    @Override
    public UserRole supports() {
        return UserRole.OWNER;
    }

    @Override
    public OrderDetailResponseDto getOrderDetail(UUID orderId, User viewer) {
        Order order = orderReader.findById(orderId);

        StoreQuery storeQuery = new StoreQuery(order.getStoreId());
        StoreInfoDto storeInfoDto = loadStoreSummaryInfo(storeQuery,
            viewer.getUserId());

        // 본인 가게 주문만
        Long storeOwnerId = storeInfoDto.ownerId();
        Long viewerUserId = viewer.getUserId();
        if (!storeOwnerId.equals(viewerUserId)) {
            log.warn(
                "OWNER는 본인 가게의 주문만 조회할 수 있습니다. : role={}, viewerId={}, storeOwnerId={}, orderId={}",
                viewer.getRole(), viewerUserId, storeOwnerId, orderId);
            throw new BusinessException(AuthErrorCode.FORBIDDEN);
        }
        return orderMapper.toDto(order);
    }

    @Override
    public OrdersResponseDto getOrders(GetOrdersRequestDto requestDto, User viewer,
        Pageable pageable) {
        return getOrdersCommon(requestDto, viewer, pageable, orderReader, orderMapper);
    }

    private StoreInfoDto loadStoreSummaryInfo(StoreQuery query, Long userId) {
        return storeReaderRepository.loadStoreSummaryInfo(query.storeId())
            .map(StoreInfoDto::from)
            .orElseThrow(() -> {
                log.warn("[Store] NOT_EXIST - storeId={}, userId={}", query.storeId(), userId);
                return new BusinessException(StoreRefErrorCode.NOT_EXIST);
            });
    }
}
