package com.spartaclub.orderplatform.domain.payment.domain.model;

import com.spartaclub.orderplatform.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "p_payments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "p_payments_seq")
    @SequenceGenerator(
        name = "p_payments_seq",  // JPA가 식별하기 위한 name
        sequenceName = "p_payments_seq", //데이터베이스에 저장되는 name
        allocationSize = 30
    )
    @UuidGenerator
    @Column(name = "paymentId", nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID paymentId;               // 결제ID

    @Column(name = "orderId", nullable = false, columnDefinition = "uuid")
    private UUID orderId;                 // 주문ID(FK)

    @Column(name = "paymentAmount", nullable = false)
    private Long paymentAmount;           // 결제금액

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 40)
    private PaymentStatus status;               // 결제상태
}
