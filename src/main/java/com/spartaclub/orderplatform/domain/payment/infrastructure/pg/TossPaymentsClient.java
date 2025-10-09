package com.spartaclub.orderplatform.domain.payment.infrastructure.pg;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class TossPaymentsClient {

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
    public boolean confirmPayment(String paymentKey, String orderId, Long amount) {
        /*실제 PG 연동하지 않기 때문에 무조건 true*/
        return true;
    }

}
