package com.spartaclub.orderplatform.domain.order.application;

import com.spartaclub.orderplatform.domain.order.application.mapper.OrderMapper;
import com.spartaclub.orderplatform.domain.order.domain.model.Order;
import com.spartaclub.orderplatform.domain.order.infrastructure.repository.OrderRepository;
import com.spartaclub.orderplatform.domain.order.presentation.dto.GetOrderDetailRequestDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.OrderDetailResponseDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.PlaceOrderRequestDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.PlaceOrderRequestDto.OrderItemRequest;
import com.spartaclub.orderplatform.domain.order.presentation.dto.PlaceOrderResponseDto;
import com.spartaclub.orderplatform.domain.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

        // 총 주문 금액, 상품 개수 집계
        for (OrderItemRequest orderItem : products) {
            //Optional<Product> product=productRepository.findById(orderItem.productId());

            totalPrice += (long) orderItem.quantity();
            productCount += orderItem.quantity();
        }

        Order order = orderMapper.toEntity(placeOrderRequestDto, totalPrice, productCount);

        orderRepository.save(order);

        return new PlaceOrderResponseDto(order.getOrderId());
    }

    //주문 상세 조회
    public OrderDetailResponseDto getOrderDetail(GetOrderDetailRequestDto requestDto) {
        return orderMapper.toDto(
            orderRepository.findById(requestDto.orderId())
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다.")));
    }
}
