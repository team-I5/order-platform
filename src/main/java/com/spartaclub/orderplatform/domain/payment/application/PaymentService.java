package com.spartaclub.orderplatform.domain.payment.application;

import com.spartaclub.orderplatform.domain.order.application.OrderService;
import com.spartaclub.orderplatform.domain.order.domain.model.Order;
import com.spartaclub.orderplatform.domain.order.domain.model.OrderStatus;
import com.spartaclub.orderplatform.domain.payment.domain.model.Payment;
import com.spartaclub.orderplatform.domain.payment.domain.model.PaymentStatus;
import com.spartaclub.orderplatform.domain.payment.infrastructure.repository.PaymentRepository;
import com.spartaclub.orderplatform.domain.payment.presentation.dto.InitPaymentRequestDto;
import com.spartaclub.orderplatform.domain.payment.presentation.dto.InitPaymentResponseDto;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;

    //결제 생성
    @Transactional
    public InitPaymentResponseDto initPayment(InitPaymentRequestDto requestDto) {
        Order order = orderService.findById(requestDto.orderId());

        // 주문 상태 검증
        if (order.getStatus() != OrderStatus.PAYMENT_PENDING) {
            throw new IllegalStateException(
                "결제를 진행할 수 없는 주문 상태입니다. (현재 상태: " + order.getStatus() + ")"
            );
        }

        //결제 금액 검증
        if (!Objects.equals(order.getTotalPrice(), requestDto.amount())) {
            throw new IllegalStateException("결제 요청 금액이 주문 총액과 일치하지 않습니다. "
                + "(주문금액: " + order.getTotalPrice() + ", 요청금액: " + requestDto.amount() + ")");
        }

        //PG사 결제 요청
        String redirectUrl = requestPaymentReady(requestDto.amount());

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
                .PgPaymentKey(PgPaymentKey)
                .PgOrderId(PgOrderId)
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

    //PG사에 결제 요청 실제 연동 X
    public String requestPaymentReady(Long amount) {
        /*PG사 결제 요청 실제 연동 하지 않으므로
         * redirectUrl 임의로 생성*/
        return "https://store.com/success?paymentKey="
            + UUID.randomUUID()
            + "&orderId=" + UUID.randomUUID()
            + "&amount=" + amount;
    }

    //PG사 결제 승인
    public boolean confirmPayment() {
        /*실제 PG 연동하지 않기 때문에 무조건 true*/
        return true;
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
