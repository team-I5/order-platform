package com.spartaclub.orderplatform.domain.order.application;

import com.spartaclub.orderplatform.domain.order.application.mapper.OrderMapper;
import com.spartaclub.orderplatform.domain.order.domain.model.Order;
import com.spartaclub.orderplatform.domain.order.domain.model.OrderProduct;
import com.spartaclub.orderplatform.domain.order.infrastructure.repository.OrderRepository;
import com.spartaclub.orderplatform.domain.order.presentation.dto.GetOrderDetailRequestDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.GetOrdersRequestDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.OrderDetailResponseDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.OrdersResponseDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.PlaceOrderRequestDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.PlaceOrderRequestDto.OrderItemRequest;
import com.spartaclub.orderplatform.domain.order.presentation.dto.PlaceOrderResponseDto;
import com.spartaclub.orderplatform.domain.product.entity.Product;
import com.spartaclub.orderplatform.domain.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ProductRepository productRepository;

    //주문 생성
    @Transactional
    public PlaceOrderResponseDto placeOrder(PlaceOrderRequestDto placeOrderRequestDto) {
        List<OrderItemRequest> products = placeOrderRequestDto.items(); // 주문 상품 리스트

        Long totalPrice = 0L;
        Integer productCount = 0;
        List<OrderProduct> orderProducts = new ArrayList<>();

        // 총 주문 금액, 상품 개수 집계, 주문-상품 엔티티 생성
        for (OrderItemRequest orderItem : products) {
            Product product = productRepository.findById(orderItem.productId())
                .orElseThrow(
                    () -> new IllegalArgumentException("상품을 찾을 수 없습니다: " + orderItem.productId()));

            totalPrice += (long) orderItem.quantity();
            productCount += orderItem.quantity();

            OrderProduct orderProduct = OrderProduct.builder()
                .product(product)
                .quantity(orderItem.quantity())
                .unitPrice(Long.valueOf(product.getPrice()))   // Product 필드 수정되면 수정하기
                .productName(product.getProductName())
                .build();

            orderProducts.add(orderProduct);
        }

        Order order = orderMapper.toEntity(placeOrderRequestDto, totalPrice, productCount);

        for (OrderProduct orderProduct : orderProducts) {
            order.addOrderProduct(orderProduct);
        }

        orderRepository.save(order);

        return new PlaceOrderResponseDto(order.getOrderId());
    }

    //주문 상세 조회
    public OrderDetailResponseDto getOrderDetail(GetOrderDetailRequestDto requestDto) {
        return orderMapper.toDto(
            orderRepository.findById(requestDto.orderId())
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다.")));
    }

    //주문 목록 조회
    public OrdersResponseDto getOrders(GetOrdersRequestDto requestDto) {

        Pageable pageable = PageRequest.of(
            requestDto.page() - 1,
            requestDto.size(),
            parseSort(requestDto.sort()));

        Page<Order> page = orderRepository.findByUser_UserId(1001L,
            pageable);  // TODO: userId 수정 필요

        List<OrderDetailResponseDto> ordersList = page.stream()
            .map(orderMapper::toDto)
            .collect(Collectors.toList());

        //TODO: Pageable Mapper 만들기
        OrdersResponseDto.PageableDto meta = orderMapper.toPageableDto(page);

        return new OrdersResponseDto(ordersList, meta);
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
}
