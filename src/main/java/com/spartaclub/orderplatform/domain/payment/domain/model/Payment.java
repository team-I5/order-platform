package com.spartaclub.orderplatform.domain.payment.domain.model;

import com.spartaclub.orderplatform.domain.order.domain.model.Order;
import com.spartaclub.orderplatform.global.domain.entity.BaseEntity;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "p_payments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@AllArgsConstructor
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
        if (this.status != PaymentStatus.AUTHORIZED) {
            throw new IllegalStateException(
                "결제를 승인할 수 없는 상태입니다. (현재 상태: " + this.status + ")"
            );
        }

        // PG 결제키 검증
        if (!this.pgPaymentKey.trim().equals(requestPgPaymentKey.trim())) {
            throw new IllegalStateException(
                "PG 결제키가 일치하지 않습니다. (저장된 키: " + this.pgPaymentKey + ", 요청 키: " + requestPgPaymentKey
                    + ")"
            );
        }

        // PG 주문번호 검증
        if (!this.pgOrderId.trim().equals(requestPgOrderId.trim())) {
            throw new IllegalStateException(
                "PG 주문번호가 일치하지 않습니다. (저장된 주문번호: " + this.pgOrderId + ", 요청 주문번호: "
                    + requestPgOrderId + ")"
            );
        }

        // 결제 금액 검증
        if (!Objects.equals(this.paymentAmount, requestAmount)) {
            throw new IllegalStateException(
                "결제 금액이 일치하지 않습니다. (저장된 금액: " + this.paymentAmount + ", 요청 금액: " + requestAmount
                    + ")"
            );
        }
    }
}

