package com.spartaclub.orderplatform.domain.order.application;

import com.spartaclub.orderplatform.domain.order.application.dto.query.OrderQuery;
import com.spartaclub.orderplatform.domain.order.application.mapper.OrderMapper;
import com.spartaclub.orderplatform.domain.order.domain.model.Order;
import com.spartaclub.orderplatform.domain.order.domain.model.OrderProduct;
import com.spartaclub.orderplatform.domain.order.domain.model.OrderStatus;
import com.spartaclub.orderplatform.domain.order.domain.repository.OrderRepository;
import com.spartaclub.orderplatform.domain.order.domain.repository.ProductReaderRepository;
import com.spartaclub.orderplatform.domain.order.domain.repository.StoreReaderRepository;
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
import com.spartaclub.orderplatform.global.application.security.UserDetailsImpl;
import com.spartaclub.orderplatform.user.domain.entity.User;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ProductReaderRepository productRepository;
    private final StoreReaderRepository storeReaderRepository;

    //주문 생성
    @Transactional
    public PlaceOrderResponseDto placeOrder(PlaceOrderRequestDto placeOrderRequestDto, User user) {
        List<OrderItemRequest> products = placeOrderRequestDto.items(); // 주문 상품 리스트

        Store store = storeReaderRepository.findById(placeOrderRequestDto.storeId())
            .orElseThrow(() -> new IllegalArgumentException(
                "음식점을 찾을 수 없습니다: " + placeOrderRequestDto.storeId()));

        Long totalPrice = 0L;
        Integer productCount = 0;
        List<OrderProduct> orderProducts = new ArrayList<>();

        // 총 주문 금액, 상품 개수 집계, 주문-상품 엔티티 생성
        for (OrderItemRequest orderItem : products) {
            Product product = productRepository.findById(orderItem.productId())
                .orElseThrow(
                    () -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + orderItem.productId()));

            totalPrice += orderItem.quantity() * product.getPrice();
            productCount += orderItem.quantity();

            OrderProduct orderProduct = OrderProduct.builder()
                .product(product)
                .quantity(orderItem.quantity())
                .unitPrice(product.getPrice())
                .productName(product.getProductName())
                .build();

            orderProducts.add(orderProduct);
        }

        //Dto -> Entity 매핑
        Order order = orderMapper.toEntity(placeOrderRequestDto,
            totalPrice,
            productCount,
            user.getUserId());

        //연관관계 형성
        for (OrderProduct orderProduct : orderProducts) {
            order.addOrderProduct(orderProduct);
        }
        order.setUser(user);
        order.setStore(store);

        orderRepository.save(order);

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
                    throw new AccessDeniedException("CUSTOMER는 본인의 주문만 조회할 수 있습니다.");
                }
            }
            case OWNER -> {
                // 본인 가게 주문만
                Long storeOwnerId = order.getStore().getUser().getUserId();
                Long viewerUserId = viewer.getUserId();
                if (!storeOwnerId.equals(viewerUserId)) {
                    throw new AccessDeniedException("OWNER는 본인 가게의 주문만 조회할 수 있습니다.");
                }
            }
            case MANAGER, MASTER -> {
                // 모두 가능: 추가 검증 없음
            }
            default -> {
                throw new AccessDeniedException("해당 역할은 주문 조회 권한이 없습니다.");
            }
        }
        return orderMapper.toDto(order);
    }

    //주문 목록 조회
    @Transactional(readOnly = true)
    public OrdersResponseDto getOrders(GetOrdersRequestDto requestDto,
        UserDetailsImpl userDetails) {
        User viewer = userDetails.getUser();

        Pageable pageable = PageRequest.of(
            requestDto.page() - 1,
            requestDto.size(),
            parseSort(requestDto.sort()));

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

    //페이지네이션 Sort 객체 생성
    private Sort parseSort(List<String> sortParams) {
        //기본값
        String defaultProperty = "createdAt";
        Sort.Direction defaultDir = Sort.Direction.DESC;
        // 정렬 허용 필드
        Set<String> allowedProperties = Set.of("createdAt", "totalPrice");

        List<Sort.Order> orders = new ArrayList<>();

        for (String param : sortParams) {
            if (param == null || param.isBlank()) {
                continue;
            }

            String[] parts = param.split(",");
            String property = parts[0].trim();
            Sort.Direction direction = (parts.length > 1 && "asc".equalsIgnoreCase(parts[1].trim()))
                ? Sort.Direction.ASC : Sort.Direction.DESC;

            if (allowedProperties.contains(property)) {
                orders.add(new Sort.Order(direction, property));
            }
        }

        return orders.isEmpty() ? Sort.by(defaultDir, defaultProperty) : Sort.by(orders);
    }

    public Order findById(UUID orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다: " + orderId));
    }
}
