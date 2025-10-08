package com.spartaclub.orderplatform.domain.payment.presentation.controller;

import com.spartaclub.orderplatform.domain.payment.application.PaymentService;
import com.spartaclub.orderplatform.domain.payment.presentation.dto.InitPaymentRequestDto;
import com.spartaclub.orderplatform.domain.payment.presentation.dto.InitPaymentResponseDto;
import com.spartaclub.orderplatform.global.presentation.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/init")
    public ResponseEntity<ApiResponse<InitPaymentResponseDto>> initPayment(
        @AuthenticationPrincipal UserDetails userDetails,
        @Valid @RequestBody InitPaymentRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(paymentService.initPayment(requestDto)));
    }
}
