package com.spartaclub.orderplatform.domain.order.presentation.controller;

import com.spartaclub.orderplatform.domain.order.application.OrderService;
import com.spartaclub.orderplatform.domain.order.presentation.dto.GetOrdersRequestDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.OrderDetailResponseDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.OrderStatusResponseDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.OrdersResponseDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.PlaceOrderRequestDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.PlaceOrderResponseDto;
import com.spartaclub.orderplatform.global.application.security.UserDetailsImpl;
import com.spartaclub.orderplatform.global.presentation.dto.ApiResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable UUID orderId) {
        return ResponseEntity.ok(
            ApiResponse.success(orderService.getOrderDetail(orderId, userDetails)));
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<OrdersResponseDto>> getOrders(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @Valid GetOrdersRequestDto requestDto) {
        return ResponseEntity.ok(
            ApiResponse.success(orderService.getOrders(requestDto, userDetails)));
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<OrderStatusResponseDto>> cancelOrder(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable UUID orderId
    ) {
        OrderStatusResponseDto response = orderService.cancelOrder(userDetails, orderId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{orderId}/accept")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<OrderStatusResponseDto>> acceptOrder(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable UUID orderId
    ) {
        OrderStatusResponseDto response = orderService.acceptOrder(userDetails, orderId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{orderId}/reject")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<OrderStatusResponseDto>> rejectOrder(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable UUID orderId
    ) {
        OrderStatusResponseDto response = orderService.rejectOrder(userDetails, orderId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
