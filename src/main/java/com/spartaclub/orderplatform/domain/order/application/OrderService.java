package com.spartaclub.orderplatform.domain.order.application;

import static java.util.function.UnaryOperator.identity;

import com.spartaclub.orderplatform.domain.order.application.command.PlaceOrderCommand;
import com.spartaclub.orderplatform.domain.order.application.mapper.OrderMapper;
import com.spartaclub.orderplatform.domain.order.application.query.OrderQuery;
import com.spartaclub.orderplatform.domain.order.domain.model.Order;
import com.spartaclub.orderplatform.domain.order.domain.model.OrderStatus;
import com.spartaclub.orderplatform.domain.order.domain.repository.OrderRepository;
import com.spartaclub.orderplatform.domain.order.domain.repository.ProductReaderRepository;
import com.spartaclub.orderplatform.domain.order.domain.repository.StoreReaderRepository;
import com.spartaclub.orderplatform.domain.order.exception.OrderErrorCode;
import com.spartaclub.orderplatform.domain.order.exception.StoreRefErrorCode;
import com.spartaclub.orderplatform.domain.order.presentation.dto.request.GetOrdersRequestDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.request.PlaceOrderRequestDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.request.PlaceOrderRequestDto.OrderItemRequest;
import com.spartaclub.orderplatform.domain.order.presentation.dto.response.OrderDetailResponseDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.response.OrderStatusResponseDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.response.OrdersResponseDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.response.OrdersResponseDto.OrderSummaryDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.response.PlaceOrderResponseDto;
import com.spartaclub.orderplatform.domain.product.domain.entity.Product;
import com.spartaclub.orderplatform.domain.store.domain.model.Store;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.global.auth.UserDetailsImpl;
import com.spartaclub.orderplatform.global.auth.exception.AuthErrorCode;
import com.spartaclub.orderplatform.global.exception.BusinessException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ProductReaderRepository productReaderRepository;
    private final StoreReaderRepository storeReaderRepository;

    //주문 생성
    @Transactional
    public PlaceOrderResponseDto placeOrder(PlaceOrderRequestDto placeOrderRequestDto, User user) {
        List<OrderItemRequest> items = placeOrderRequestDto.items(); // 주문 상품 리스트

        //상품 Map
        Map<UUID, Product> productMap = loadProductMap(items);
        //상품Id, 수량 commands
        List<PlaceOrderCommand> commands = items.stream().map(orderMapper::toCommand).toList();

        Store store = loadStore(placeOrderRequestDto.storeId(), user.getUserId());

        Order order = Order.place(user, store, commands, productMap, placeOrderRequestDto.address(),
            placeOrderRequestDto.memo());

        order = orderRepository.save(order);

        return new PlaceOrderResponseDto(order.getOrderId());
    }

    //주문 상세 조회
    @Transactional(readOnly = true)
    public OrderDetailResponseDto getOrderDetail(UUID orderId,
        UserDetailsImpl userDetails) {
        User viewer = userDetails.getUser();
        Order order = findById(orderId);

        switch (viewer.getRole()) {
            case CUSTOMER -> {
                // 본인 주문만
                Long ownerUserId = order.getUser().getUserId();
                Long viewerUserId = viewer.getUserId();
                if (!ownerUserId.equals(viewerUserId)) {
                    log.warn(
                        "CUSTOMER는 본인의 주문만 조회할 수 있습니다. : role={}, viewerId={}, ownerId={}, orderId={}",
                        viewer.getRole(), viewerUserId, ownerUserId, orderId);
                    throw new BusinessException(AuthErrorCode.FORBIDDEN);
                }
            }
            case OWNER -> {
                // 본인 가게 주문만
                Long storeOwnerId = order.getStore().getUser().getUserId();
                Long viewerUserId = viewer.getUserId();
                if (!storeOwnerId.equals(viewerUserId)) {
                    log.warn(
                        "OWNER는 본인 가게의 주문만 조회할 수 있습니다. : role={}, viewerId={}, storeOwnerId={}, orderId={}",
                        viewer.getRole(), viewerUserId, storeOwnerId, orderId);
                    throw new BusinessException(AuthErrorCode.FORBIDDEN);
                }
            }
            case MANAGER, MASTER -> {
                // 모두 가능: 추가 검증 없음
            }
            default -> {
                log.warn("해당 역할은 주문 조회 권한이 없습니다. : role={}, viewerId={}, orderId={}",
                    viewer.getRole(), viewer.getUserId(), orderId);
                throw new BusinessException(AuthErrorCode.FORBIDDEN);
            }
        }
        return orderMapper.toDto(order);
    }

    //주문 목록 조회
    @Transactional(readOnly = true)
    public OrdersResponseDto getOrders(GetOrdersRequestDto requestDto,
        UserDetailsImpl userDetails,
        Pageable pageable) {
        User viewer = userDetails.getUser();

        //조회
        OrderQuery orderQuery = new OrderQuery(requestDto.status(), viewer);
        Page<Order> orders = orderRepository.findAll(orderQuery, pageable);

        List<OrderSummaryDto> ordersList = orders.getContent().stream()
            .map(orderMapper::toSummaryDto)
            .collect(Collectors.toList());

        OrdersResponseDto.PageableDto meta = orderMapper.toPageableDto(orders);

        return new OrdersResponseDto(ordersList, meta);
    }

    //주문 취소
    @Transactional
    public OrderStatusResponseDto cancelOrder(UserDetailsImpl userDetails, UUID orderId) {
        User user = userDetails.getUser();
        Order order = findById(orderId);

        //상태 검증 및 변경, 5분 이내의 주문만 취소 가능
        order.checkCancelable();
        order.changeStatus(OrderStatus.CANCELED);

        return OrderStatusResponseDto.ofCanceled(orderId);
    }

    //주문 승인
    @Transactional
    public OrderStatusResponseDto acceptOrder(UserDetailsImpl userDetails, UUID orderId) {
        Order order = findById(orderId);

        //상태 검증 및 변경
        order.checkAcceptable();
        order.changeStatus(OrderStatus.ACCEPTED);

        return OrderStatusResponseDto.ofAccepted(orderId);
    }

    //주문 거부
    @Transactional
    public OrderStatusResponseDto rejectOrder(UserDetailsImpl userDetails, UUID orderId) {
        Order order = findById(orderId);

        //상태 검증 및 변경
        order.checkRejectable();
        order.changeStatus(OrderStatus.REJECTED);

        return OrderStatusResponseDto.ofRejected(orderId);
    }

    //주문 배달 완료
    @Transactional
    public OrderStatusResponseDto completeDelivery(UserDetailsImpl userDetails, UUID orderId) {
        Order order = findById(orderId);

        //상태 검증 및 변경
        order.checkDeliverable();
        order.changeStatus(OrderStatus.DELIVERED);

        return OrderStatusResponseDto.ofDelivered(orderId);
    }

    public Order findById(UUID orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> {
                log.warn("[Order] NOT_EXIST - orderId={}", orderId);
                return new BusinessException(OrderErrorCode.NOT_EXIST);
            });
    }

    private Store loadStore(UUID storeId, Long userId) {
        return storeReaderRepository.findById(storeId)
            .orElseThrow(() -> {
                log.warn("[Store] NOT_EXIST - storeId={}, userId={}", storeId, userId);
                return new BusinessException(StoreRefErrorCode.NOT_EXIST);
            });
    }

    // 상품 로딩(IN) → id->Product 맵
    private Map<UUID, Product> loadProductMap(List<OrderItemRequest> items) {
        List<UUID> ids = items.stream()
            .map(OrderItemRequest::productId)
            .toList();

        return productReaderRepository.findByProductIdIn(ids).stream()
            .collect(Collectors.toMap(Product::getProductId, identity()));
    }
}
