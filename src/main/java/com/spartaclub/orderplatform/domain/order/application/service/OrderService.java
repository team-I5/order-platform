package com.spartaclub.orderplatform.domain.order.application.service;

import static java.util.function.UnaryOperator.identity;

import com.spartaclub.orderplatform.domain.order.application.command.PlaceOrderCommand;
import com.spartaclub.orderplatform.domain.order.application.dto.StoreInfoDto;
import com.spartaclub.orderplatform.domain.order.application.mapper.OrderMapper;
import com.spartaclub.orderplatform.domain.order.application.query.StoreQuery;
import com.spartaclub.orderplatform.domain.order.domain.model.Order;
import com.spartaclub.orderplatform.domain.order.domain.model.OrderStatus;
import com.spartaclub.orderplatform.domain.order.domain.repository.OrderRepository;
import com.spartaclub.orderplatform.domain.order.domain.repository.ProductReaderRepository;
import com.spartaclub.orderplatform.domain.order.domain.repository.StoreReaderRepository;
import com.spartaclub.orderplatform.domain.order.exception.OrderErrorCode;
import com.spartaclub.orderplatform.domain.order.exception.StoreRefErrorCode;
import com.spartaclub.orderplatform.domain.order.presentation.dto.request.PlaceOrderRequestDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.request.PlaceOrderRequestDto.OrderItemRequest;
import com.spartaclub.orderplatform.domain.order.presentation.dto.response.OrderStatusResponseDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.response.PlaceOrderResponseDto;
import com.spartaclub.orderplatform.domain.product.domain.entity.Product;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.global.auth.UserDetailsImpl;
import com.spartaclub.orderplatform.global.exception.BusinessException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

        StoreQuery storeQuery = new StoreQuery(placeOrderRequestDto.storeId());
        StoreInfoDto storeInfoDto = loadStoreSummaryInfo(storeQuery, user.getUserId());

        Order order = Order.place(user, storeInfoDto.storeId(), commands, productMap,
            placeOrderRequestDto.address(),
            placeOrderRequestDto.memo());

        order = orderRepository.save(order);

        return new PlaceOrderResponseDto(order.getOrderId());
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

    // 상품 로딩(IN) → id->Product 맵
    private Map<UUID, Product> loadProductMap(List<OrderItemRequest> items) {
        List<UUID> ids = items.stream()
            .map(OrderItemRequest::productId)
            .toList();

        return productReaderRepository.findByProductIdIn(ids).stream()
            .collect(Collectors.toMap(Product::getProductId, identity()));
    }

    public Order findById(UUID orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> {
                log.warn("[OrderReader] NOT_EXIST - orderId={}", orderId);
                return new BusinessException(OrderErrorCode.NOT_EXIST);
            });
    }

    public StoreInfoDto loadStoreSummaryInfo(StoreQuery query, Long userId) {
        return storeReaderRepository.loadStoreSummaryInfo(query.storeId())
            .map(StoreInfoDto::from)
            .orElseThrow(() -> {
                log.warn("[Store] NOT_EXIST - storeId={}, userId={}", query.storeId(), userId);
                return new BusinessException(StoreRefErrorCode.NOT_EXIST);
            });
    }
}
