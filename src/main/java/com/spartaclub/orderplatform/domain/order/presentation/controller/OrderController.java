package com.spartaclub.orderplatform.domain.order.presentation.controller;

import com.spartaclub.orderplatform.domain.order.application.OrderService;
import com.spartaclub.orderplatform.domain.order.presentation.dto.GetOrderDetailRequestDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.GetOrdersRequestDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.OrderDetailResponseDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.OrdersResponseDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.PlaceOrderRequestDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.PlaceOrderResponseDto;
import com.spartaclub.orderplatform.global.application.security.UserDetailsImpl;
import com.spartaclub.orderplatform.global.presentation.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 주문 생성 API
    @PostMapping("")
    public ResponseEntity<ApiResponse<PlaceOrderResponseDto>> placeOrder(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @Valid @RequestBody PlaceOrderRequestDto requestDto) {

        PlaceOrderResponseDto responseDto = orderService.placeOrder(requestDto,
            userDetails.getUser());

        String location = "/v1/orders/" + responseDto.orderId().toString(); // 헤더 Location

        return ResponseEntity.status(HttpStatus.CREATED)
            .header("Location", location)
            .body(ApiResponse.success(responseDto));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDetailResponseDto>> getOrderDetail(
        @Valid GetOrderDetailRequestDto requestDto) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrderDetail(requestDto)));
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<OrdersResponseDto>> getOrders(
        @Valid GetOrdersRequestDto requestDto) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrders(requestDto)));
    }
}
