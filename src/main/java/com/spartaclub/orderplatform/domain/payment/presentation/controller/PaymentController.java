package com.spartaclub.orderplatform.domain.payment.presentation.controller;

import com.spartaclub.orderplatform.domain.payment.application.PaymentService;
import com.spartaclub.orderplatform.domain.payment.presentation.dto.request.CancelPaymentRequestDto;
import com.spartaclub.orderplatform.domain.payment.presentation.dto.request.ConfirmPaymentRequestDto;
import com.spartaclub.orderplatform.domain.payment.presentation.dto.request.GetPaymentsListRequestDto;
import com.spartaclub.orderplatform.domain.payment.presentation.dto.request.InitPaymentRequestDto;
import com.spartaclub.orderplatform.domain.payment.presentation.dto.response.InitPaymentResponseDto;
import com.spartaclub.orderplatform.domain.payment.presentation.dto.response.PaymentDetailResponseDto;
import com.spartaclub.orderplatform.domain.payment.presentation.dto.response.PaymentsListResponseDto;
import com.spartaclub.orderplatform.global.auth.UserDetailsImpl;
import com.spartaclub.orderplatform.global.presentation.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
@RequiredArgsConstructor
@RequestMapping("/v1/payments")
@Tag(name = "Payment", description = "결제 API")
public class PaymentController {

    private final PaymentService paymentService;

    // 결제 생성 및 요청
    @Operation(
        summary = "결제 생성(Init)",
        description = """
            주문에 대한 결제를 초기화합니다.
            - PG에 결제 세션/키를 생성하거나 승인 준비 상태로 전환합니다.
            - 성공 시 201 Created와 함께 초기화 결과를 반환합니다.
            """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "결제 초기화 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = InitPaymentResponseDto.class)
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 본문 검증 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "주문/가맹점 리소스 없음")
    })
    @PostMapping("/init")
    public ResponseEntity<ApiResponse<InitPaymentResponseDto>> initPayment(
        @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "결제 초기화 요청 본문",
            content = @Content(schema = @Schema(implementation = InitPaymentRequestDto.class))
        )
        @Valid @RequestBody InitPaymentRequestDto requestDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(paymentService.initPayment(requestDto)));
    }

    // 결제 승인
    @Operation(
        summary = "결제 승인(Confirm)",
        description = """
            결제 승인(캡처) 처리합니다.
            - 결제 ID와 승인에 필요한 파라미터를 받아 PG에 승인 요청을 수행합니다.
            - 성공 시 200 OK와 공통 성공 응답을 반환합니다.
            """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "승인 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 본문 검증 실패 / 승인 불가 상태"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "결제 리소스 없음")
    })
    @PostMapping("/{paymentId}/confirm")
    public ResponseEntity<ApiResponse<Void>> confirmPayment(
        @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails,
        @Parameter(name = "paymentId", description = "결제 ID", in = ParameterIn.PATH, required = true,
            example = "7b8c2f2a-2b3c-4d5e-8f90-1a2b3c4d5e6f")
        @PathVariable UUID paymentId,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "결제 승인 요청 본문",
            content = @Content(schema = @Schema(implementation = ConfirmPaymentRequestDto.class))
        )
        @Valid @RequestBody ConfirmPaymentRequestDto requestDto
    ) {
        paymentService.confirmPayment(requestDto, paymentId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    // 결제 취소
    @Operation(
        summary = "결제 취소(Cancel)",
        description = """
            이미 승인된 결제를 취소합니다.
            - 성공 시 200 OK와 공통 성공 응답을 반환합니다.
            """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "취소 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "취소 불가 상태 / 요청 본문 검증 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "결제 리소스 없음")
    })
    @PostMapping("/{paymentId}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelPayment(
        @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails,
        @Parameter(name = "paymentId", description = "결제 ID", in = ParameterIn.PATH, required = true)
        @PathVariable UUID paymentId,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "결제 취소 요청 본문",
            content = @Content(schema = @Schema(implementation = CancelPaymentRequestDto.class))
        )
        @Valid @RequestBody CancelPaymentRequestDto requestDto
    ) {
        paymentService.cancelPayment(requestDto, paymentId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    // 결제 상세 조회
    @Operation(
        summary = "결제 상세 조회",
        description = "결제 ID로 단건 결제 상세를 조회합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = PaymentDetailResponseDto.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "접근 권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "결제 없음")
    })
    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentDetailResponseDto>> getPaymentDetail(
        @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails,
        @Parameter(name = "paymentId", description = "결제 ID", in = ParameterIn.PATH, required = true)
        @PathVariable UUID paymentId
    ) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getPaymentDetail(paymentId)));
    }

    // 전체 결제 조회 (권한 : MANAGER, MASTER)
    @Operation(
        summary = "결제 목록 조회(관리자)",
        description = """
            관리자 권한으로 결제 목록을 조건/정렬/페이지네이션하여 조회합니다.
            - 권한: MANAGER, MASTER
            - 페이지 파라미터: page(0부터), size, sort(예: createdAt,desc)
            """
    )
    @PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(schema = @Schema(implementation = PaymentsListResponseDto.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "관리자 권한 필요")
    })
    @GetMapping("")
    public ResponseEntity<ApiResponse<PaymentsListResponseDto>> getPaymentsList(
        @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails,
        @Parameter(
            description = "결제 상태 필터(aliases: statuses, status). 미지정 시 전체.",
            name = "statuses/status",
            required = false
        )
        @ModelAttribute GetPaymentsListRequestDto requestDto,
        @ParameterObject Pageable pageable
    ) {
        PaymentsListResponseDto response =
            paymentService.getPayments(requestDto, userDetails.getUser(), pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}