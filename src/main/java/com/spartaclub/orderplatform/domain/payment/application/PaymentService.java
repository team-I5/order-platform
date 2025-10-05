package com.spartaclub.orderplatform.domain.payment.application;

import com.spartaclub.orderplatform.domain.order.domain.model.Order;
import com.spartaclub.orderplatform.domain.payment.domain.model.Payment;
import com.spartaclub.orderplatform.domain.payment.domain.model.PaymentStatus;
import com.spartaclub.orderplatform.domain.payment.infrastructure.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    //결제 생성
    @Transactional(propagation = Propagation.MANDATORY)
    // 트랜잭션이 반드시 필요하고 기존 트랜잭션에 참여
    public Payment createPending(Order order, long amount) {
        Payment payment = Payment.builder()
            .order(order)
            .paymentAmount(amount)
            .status(PaymentStatus.PAYMENT_PENDING)
            .build();
        return paymentRepository.save(payment);
    }

    //결제 승인 (PG 결제 요청)
    public boolean confirmPayment() {
        /*실제 PG 연동하지 않기 때문에 무조건 true*/
        return true;
    }

}
