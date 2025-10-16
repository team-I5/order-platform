package com.spartaclub.orderplatform.domain.order.presentation.controller;

import com.spartaclub.orderplatform.domain.order.application.OrderService;
import com.spartaclub.orderplatform.domain.order.presentation.dto.request.GetOrdersRequestDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.request.PlaceOrderRequestDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.response.OrderDetailResponseDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.response.OrderStatusResponseDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.response.OrdersResponseDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.response.PlaceOrderResponseDto;
import com.spartaclub.orderplatform.global.auth.UserDetailsImpl;
import com.spartaclub.orderplatform.global.presentation.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Order", description = "주문 API")
public class OrderController {

    private final OrderService orderService;

    // 주문 생성 API
    @Operation(
        summary = "주문 생성",
        description = """
            새 주문을 생성합니다.
            - 성공 시 201과 함께 생성된 주문의 ID를 반환하며, Location 헤더에 상세 조회 URL이 포함됩니다.
            - 요청 본문은 음식점 ID, 주문 품목 리스트, 수령지, 요청사항 등으로 구성됩니다.
            """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "주문 생성 성공",
            headers = {
                @Header(name = "Location", description = "생성된 주문의 조회 경로(예: /v1/orders/{orderId})")
            },
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = PlaceOrderResponseDto.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효성 검증 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "가게/상품 없음")
    })
    @PostMapping("")
    public ResponseEntity<ApiResponse<PlaceOrderResponseDto>> placeOrder(
        @Parameter(hidden = true)
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "주문 생성 요청 본문",
            content = @Content(schema = @Schema(implementation = PlaceOrderRequestDto.class))
        )
        @Valid @RequestBody PlaceOrderRequestDto requestDto
    ) {
        PlaceOrderResponseDto responseDto =
            orderService.placeOrder(requestDto, userDetails.getUser());

        String location = "/v1/orders/" + responseDto.orderId();
        return ResponseEntity.status(HttpStatus.CREATED)
            .header("Location", location)
            .body(ApiResponse.success(responseDto));
    }

    // 주문 상세 조회
    @Operation(
        summary = "주문 상세 조회",
        description = "주문 ID로 단건 주문 상세를 조회합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = OrderDetailResponseDto.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "접근 권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "주문 없음")
    })
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDetailResponseDto>> getOrderDetail(
        @Parameter(hidden = true)
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @Parameter(name = "orderId", description = "주문 ID", in = ParameterIn.PATH, required = true, example = "7b8c2f2a-2b3c-4d5e-8f90-1a2b3c4d5e6f")
        @PathVariable UUID orderId
    ) {
        return ResponseEntity.ok(
            ApiResponse.success(orderService.getOrderDetail(orderId, userDetails)));
    }

    // 주문 목록 조회
    @Operation(
        summary = "주문 목록 조회(검색/정렬/페이지네이션)",
        description = """
            주문 목록을 조건에 따라 페이지네이션 조회합니다.
            - 상태(statuses/status) 필터는 선택입니다.
            - 페이지 파라미터: page(0부터), size, sort(예: createdAt,desc)
            """
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = OrdersResponseDto.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @GetMapping("")
    public ResponseEntity<ApiResponse<OrdersResponseDto>> getOrders(
        @Parameter(hidden = true)
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @Parameter(
            description = "주문 상태 필터(aliases: statuses, status). 미지정 시 전체.",
            name = "statuses/status",
            required = false
        )
        @ModelAttribute GetOrdersRequestDto requestDto,
        @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(
            ApiResponse.success(orderService.getOrders(requestDto, userDetails, pageable)));
    }

    // 주문 취소
    @Operation(
        summary = "주문 취소",
        description = "취소 가능 상태의 주문을 취소합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "취소 처리 성공",
            content = @Content(schema = @Schema(implementation = OrderStatusResponseDto.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "취소 불가 상태"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "주문 없음")
    })
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<OrderStatusResponseDto>> cancelOrder(
        @Parameter(hidden = true)
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @Parameter(name = "orderId", description = "주문 ID", in = ParameterIn.PATH, required = true)
        @PathVariable UUID orderId
    ) {
        OrderStatusResponseDto response = orderService.cancelOrder(userDetails, orderId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 주문 승인 (OWNER)
    @Operation(
        summary = "주문 승인(점주)",
        description = "점주 권한으로 접수 대기 주문을 승인합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "승인 성공",
            content = @Content(schema = @Schema(implementation = OrderStatusResponseDto.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "점주 권한 필요"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "주문 없음")
    })
    @PostMapping("/{orderId}/accept")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<OrderStatusResponseDto>> acceptOrder(
        @Parameter(hidden = true)
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @Parameter(name = "orderId", description = "주문 ID", in = ParameterIn.PATH, required = true)
        @PathVariable UUID orderId
    ) {
        OrderStatusResponseDto response = orderService.acceptOrder(userDetails, orderId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 주문 거부 (OWNER)
    @Operation(
        summary = "주문 거부(점주)",
        description = "점주 권한으로 주문을 거부합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "거부 성공",
            content = @Content(schema = @Schema(implementation = OrderStatusResponseDto.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "점주 권한 필요"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "주문 없음")
    })
    @PostMapping("/{orderId}/reject")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<OrderStatusResponseDto>> rejectOrder(
        @Parameter(hidden = true)
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @Parameter(name = "orderId", description = "주문 ID", in = ParameterIn.PATH, required = true)
        @PathVariable UUID orderId
    ) {
        OrderStatusResponseDto response = orderService.rejectOrder(userDetails, orderId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 주문 배달 완료 (OWNER)
    @Operation(
        summary = "주문 배달 완료(점주)",
        description = "점주 권한으로 배달 완료 처리합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "배달 완료 처리 성공",
            content = @Content(schema = @Schema(implementation = OrderStatusResponseDto.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "점주 권한 필요"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "주문 없음")
    })
    @PostMapping("/{orderId}/complete-delivery")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<OrderStatusResponseDto>> completeDelivery(
        @Parameter(hidden = true)
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @Parameter(name = "orderId", description = "주문 ID", in = ParameterIn.PATH, required = true)
        @PathVariable UUID orderId
    ) {
        OrderStatusResponseDto response = orderService.completeDelivery(userDetails, orderId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
