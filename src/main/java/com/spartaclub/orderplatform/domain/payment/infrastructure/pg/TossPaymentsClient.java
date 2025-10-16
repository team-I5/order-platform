package com.spartaclub.orderplatform.domain.payment.infrastructure.pg;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Slf4j
public class TossPaymentsClient {

    public TossPaymentsClient(WebClient webClient) {
        this.webClient = webClient;
    }

    @Qualifier("tossWebClient")
    private final WebClient webClient;

    //PG사에 결제 요청 실제 연동 X
    public String requestPaymentReady(Long amount) {
        log.info("[TossPaymentsClient] 결제 준비 요청 - amount={}", amount);
        /*PG사 결제 요청 실제 연동 하지 않으므로
         * redirectUrl 임의로 생성*/
        return "https://store.com/success?paymentKey="
            + UUID.randomUUID()
            + "&orderId=" + UUID.randomUUID()
            + "&amount=" + amount;
    }

    //PG사 결제 승인
    public boolean confirmPayment(String paymentKey, String orderId, Long amount) {
        log.info("[TossPaymentsClient] 결제 승인 요청 - paymentKey={}, orderId={}, amount={}",
            paymentKey, orderId, amount);
        // WebClient로 실제 연동 시:
        /*
        return webClient.post()
            .uri("/v1/payments/confirm")
            .bodyValue(Map.of(
                "paymentKey", paymentKey,
                "orderId", orderId,
                "amount", amount
            ))
            .retrieve()
            .bodyToMono(Boolean.class)
            .block();
        */

        /*실제 PG 연동하지 않기 때문에 무조건 true*/
        return true;
    }

    //결제 환불 요청
    public boolean cancelPayment(String paymentKey, String cancelReason) {
        log.info("[TossPaymentsClient] 결제 취소 요청 - paymentKey={}, reason={}", paymentKey,
            cancelReason);

        // 실제 연동 시 예시:
        /*
        return webClient.post()
            .uri("/v1/payments/cancel")
            .bodyValue(Map.of(
                "paymentKey", paymentKey,
                "cancelReason", cancelReason
            ))
            .retrieve()
            .bodyToMono(Boolean.class)
            .block();
        */

        /*실제 PG 연동하지 않기 때문에 무조건 true*/
        return true;
    }

}
