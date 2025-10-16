package com.spartaclub.orderplatform.domain.payment.domain.model;

import com.spartaclub.orderplatform.domain.order.domain.model.Order;
import com.spartaclub.orderplatform.domain.payment.exception.PaymentErrorCode;
import com.spartaclub.orderplatform.global.domain.entity.BaseEntity;
import com.spartaclub.orderplatform.global.exception.BusinessException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "p_payments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Slf4j
public class Payment extends BaseEntity {

    @Id
    @UuidGenerator
    @Column(name = "payment_id", nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID paymentId;               // 결제ID

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, columnDefinition = "uuid")
    private Order order;     // 주문 엔티티 연관관계 1:1

    @Column(name = "payment_amount", nullable = false)
    private Long paymentAmount;           // 결제금액

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 40)
    private PaymentStatus status;               // 결제상태

    @Column(name = "pg_payment_key")
    private String pgPaymentKey;

    @Column(name = "pg_order_id")
    private String pgOrderId;

    public void changeStatus(PaymentStatus status) {
        this.status = status;
    }

    public void validateApproval(String requestPgPaymentKey, String requestPgOrderId,
        Long requestAmount) {
        // 결제 상태 검증
        if (status.isNotAuthorized()) {
            log.warn("[Payment-Validate] 결제 승인 불가 상태 감지 - paymentId={}, currentStatus={}",
                paymentId, status);
            throw new BusinessException(PaymentErrorCode.INVALID_STATUS_FOR_APPROVAL);
        }

        validatePgPaymentKey(requestPgPaymentKey);

        // PG 주문번호 검증
        if (!this.pgOrderId.trim().equals(requestPgOrderId.trim())) {
            log.warn("[Payment-Validate] PG 주문번호 불일치 - storedPgOrderId={}, requestPgOrderId={}",
                pgOrderId, requestPgOrderId);
            throw new BusinessException(PaymentErrorCode.PG_ORDER_ID_MISMATCH);
        }

        // 결제 금액 검증
        if (!Objects.equals(this.paymentAmount, requestAmount)) {
            log.warn(
                "[Payment-Validate] 결제 금액 불일치 - paymentId={}, storedAmount={}, requestAmount={}",
                paymentId, paymentAmount, requestAmount);
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_AMOUNT);
        }
    }

    //결제 취소 검증
    public void checkCancelable(String requestPgPaymentKey) {
        //결제 상태 검증
        if (status.isNotCaptured()) {
            log.warn("[Payment-Validate] 결제 취소 불가 상태 - paymentId={}, currentStatus={}",
                paymentId, status);
            throw new BusinessException(PaymentErrorCode.INVALID_STATUS_FOR_CANCEL);
        }

        //결제 키 검증
        validatePgPaymentKey(requestPgPaymentKey);
    }

    //결제 키 검증
    public void validatePgPaymentKey(String requestPgPaymentKey) {
        if (this.pgPaymentKey == null || requestPgPaymentKey == null) {
            log.warn("[Payment-Validate] PG 결제키 누락 - paymentId={}, storedKey={}, requestKey={}",
                paymentId, pgPaymentKey, requestPgPaymentKey);
            throw new BusinessException(PaymentErrorCode.MISSING_PG_PAYMENT_KEY);
        }

        if (!this.pgPaymentKey.trim().equals(requestPgPaymentKey.trim())) {
            log.warn("[Payment-Validate] PG 결제키 불일치 - paymentId={}, storedKey={}, requestKey={}",
                paymentId, pgPaymentKey, requestPgPaymentKey);
            throw new BusinessException(PaymentErrorCode.PG_PAYMENT_KEY_MISMATCH);
        }
    }

    public static Payment ofStatus(Order order, PaymentStatus status, Long paymentAmount,
        String PgPaymentKey, String PgOrderId) {
        Payment payment = new Payment();
        payment.order = order;
        payment.status = status;
        payment.paymentAmount = paymentAmount;
        payment.pgPaymentKey = PgPaymentKey;
        payment.pgOrderId = PgOrderId;
        return payment;
    }

    public static Payment ofStatus(Order order, PaymentStatus status, Long paymentAmount) {
        Payment payment = new Payment();
        payment.order = order;
        payment.status = status;
        payment.paymentAmount = paymentAmount;
        return payment;
    }
}

