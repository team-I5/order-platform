package com.spartaclub.orderplatform.domain.payment.application.listener;

import com.spartaclub.orderplatform.domain.order.domain.event.PaymentRequestedEvent;
import com.spartaclub.orderplatform.domain.order.infrastructure.repository.OrderRepository;
import com.spartaclub.orderplatform.domain.payment.application.PaymentService;
import com.spartaclub.orderplatform.domain.payment.infrastructure.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventHandler {

    private final PaymentService paymentService;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    // 주문 생성 커밋 이후 결제 요청
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)    // 새로운 트랜잭션 필요
    public void onPaymentRequested(PaymentRequestedEvent e) {
        log.info("Received PaymentRequested event {}", e);

        // pg 결제 요청 호출 연동X
//        boolean success = paymentService.confirmPayment();
//        Payment p = paymentRepository.findById(e.paymentId())
//            .orElseThrow(() -> new RuntimeException("Payment not found"));
//        Order o = orderRepository.findById(e.orderId())
//            .orElseThrow(() -> new RuntimeException("Order not found"));
//
//        if (success) {
//            p.changeStatus(PaymentStatus.CAPTURED);
//            o.changeStatus(OrderStatus.PAID);
//        } else {
//            p.changeStatus(PaymentStatus.FAILED);
//            o.changeStatus(OrderStatus.CANCELED);
//        }
//
//        paymentRepository.save(p);
//        orderRepository.save(o);
    }
}
