package com.spartaclub.orderplatform.domain.payment.presentation.controller;

import com.spartaclub.orderplatform.domain.payment.application.PaymentService;
import com.spartaclub.orderplatform.domain.payment.presentation.dto.request.CancelPaymentRequestDto;
import com.spartaclub.orderplatform.domain.payment.presentation.dto.request.ConfirmPaymentRequestDto;
import com.spartaclub.orderplatform.domain.payment.presentation.dto.request.GetPaymentsListRequestDto;
import com.spartaclub.orderplatform.domain.payment.presentation.dto.request.InitPaymentRequestDto;
import com.spartaclub.orderplatform.domain.payment.presentation.dto.response.InitPaymentResponseDto;
import com.spartaclub.orderplatform.domain.payment.presentation.dto.response.PaymentDetailResponseDto;
import com.spartaclub.orderplatform.domain.payment.presentation.dto.response.PaymentsListResponseDto;
import com.spartaclub.orderplatform.global.application.security.UserDetailsImpl;
import com.spartaclub.orderplatform.global.presentation.dto.ApiResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    //결제 생성 및 요청
    @PostMapping("/init")
    public ResponseEntity<ApiResponse<InitPaymentResponseDto>> initPayment(
        @AuthenticationPrincipal UserDetails userDetails,
        @Valid @RequestBody InitPaymentRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(paymentService.initPayment(requestDto)));
    }

    //결제 승인
    @PostMapping("/{paymentId}/confirm")
    public ResponseEntity<ApiResponse<Void>> confirmPayment(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable UUID paymentId,
        @Valid @RequestBody ConfirmPaymentRequestDto requestDto) {
        paymentService.confirmPayment(requestDto, paymentId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    //결제 취소
    @PostMapping("/{paymentId}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelPayment(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable UUID paymentId,
        @Valid @RequestBody CancelPaymentRequestDto requestDto) {
        paymentService.cancelPayment(requestDto, paymentId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    //결제 상세 조회
    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentDetailResponseDto>> getPaymentDetail(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable UUID paymentId) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getPaymentDetail(paymentId)));
    }

    //전체 결제 조회 (권한 : MANAGER, MASTER)
    @GetMapping("")
    @PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
    public ResponseEntity<ApiResponse<PaymentsListResponseDto>> getPaymentsList(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @Valid GetPaymentsListRequestDto requestDto
    ) {
        PaymentsListResponseDto response = paymentService.getPayments(requestDto,
            userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
