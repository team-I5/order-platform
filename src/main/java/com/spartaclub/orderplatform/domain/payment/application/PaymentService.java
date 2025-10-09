package com.spartaclub.orderplatform.domain.payment.application;

import com.spartaclub.orderplatform.domain.order.application.OrderService;
import com.spartaclub.orderplatform.domain.order.domain.model.Order;
import com.spartaclub.orderplatform.domain.payment.domain.model.Payment;
import com.spartaclub.orderplatform.domain.payment.domain.model.PaymentStatus;
import com.spartaclub.orderplatform.domain.payment.infrastructure.pg.TossPaymentsClient;
import com.spartaclub.orderplatform.domain.payment.infrastructure.repository.PaymentRepository;
import com.spartaclub.orderplatform.domain.payment.presentation.dto.ConfirmPaymentRequestDto;
import com.spartaclub.orderplatform.domain.payment.presentation.dto.InitPaymentRequestDto;
import com.spartaclub.orderplatform.domain.payment.presentation.dto.InitPaymentResponseDto;
import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final TossPaymentsClient tossPaymentsClient;

    //결제 생성
    @Transactional
    public InitPaymentResponseDto initPayment(InitPaymentRequestDto requestDto) {
        Order order = orderService.findById(requestDto.orderId());

        //주문 상태 및 결제 금액 검증
        order.validatePaymentAvailable(requestDto.amount());

        //PG사 결제 요청
        String redirectUrl = tossPaymentsClient.requestPaymentReady(requestDto.amount());

        //리다이렉트 URL 파싱
        String[] redirectUrlParts = parseRedirectUrl(redirectUrl);
        String result = redirectUrlParts[0];
        String PgPaymentKey = redirectUrlParts[1];
        String PgOrderId = redirectUrlParts[2];

        if (result.equals("success")) {
            Payment payment = Payment.builder()
                .order(order)
                .paymentAmount(requestDto.amount())
                .status(PaymentStatus.AUTHORIZED)
                .pgPaymentKey(PgPaymentKey)
                .pgOrderId(PgOrderId)
                .build();

            paymentRepository.save(payment);
            return new InitPaymentResponseDto(payment.getPaymentId(), redirectUrl, PgPaymentKey,
                PgOrderId);
        } else {
            Payment payment = Payment.builder()
                .order(order)
                .paymentAmount(requestDto.amount())
                .status(PaymentStatus.FAILED)
                .build();

            paymentRepository.save(payment);
            return new InitPaymentResponseDto(payment.getPaymentId(), redirectUrl, null, null);
        }
    }

    @Transactional
    public void confirmPayment(ConfirmPaymentRequestDto requestDto, UUID paymentId) {
        Order order = orderService.findById(requestDto.orderId());

        //주문 상태 및 결제 금액 검증
        order.validatePaymentAvailable(requestDto.amount());

        Payment payment = findById(paymentId);

        //결제 상태, PG 결제키, PG orderId, 금액 검증
        payment.validateApproval(requestDto.pgPaymentKey(), requestDto.pgOrderId(),
            requestDto.amount());

        boolean success = tossPaymentsClient.confirmPayment(requestDto.pgPaymentKey(),
            requestDto.pgOrderId(), requestDto.amount());

        if (success) {
            payment.changeStatus(PaymentStatus.CAPTURED);
        }
//        else {
//            payment.changeStatus(PaymentStatus.FAILED);
//        }
    }

    public Payment findById(UUID paymentId) {
        return paymentRepository.findById(paymentId)
            .orElseThrow(() -> new EntityNotFoundException("결제 정보를 찾을 수 없습니다: " + paymentId));
    }

    private String[] parseRedirectUrl(String redirectUrl) {
        String[] resultParts = new String[3];
        try {
            // 결과 구분
            resultParts[0] = redirectUrl.contains("success") ? "success" : "fail";

            // 쿼리 파라미터 부분만 분리
            String[] split = redirectUrl.split("\\?");
            if (split.length < 2) {
                throw new IllegalArgumentException("잘못된 redirectUrl 형식입니다: " + redirectUrl);
            }

            // 각 파라미터를 '=' 기준으로 분리
            String[] params = split[1].split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                String key = keyValue[0];
                String value = keyValue[1];
                if (key.equals("paymentKey")) {
                    resultParts[1] = value;
                } else if (key.equals("orderId")) {
                    resultParts[2] = value;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Redirect URL 파싱 중 오류 발생: " + e.getMessage(), e);
        }
        return resultParts;
    }
}
