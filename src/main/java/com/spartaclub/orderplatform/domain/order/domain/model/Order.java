package com.spartaclub.orderplatform.domain.order.domain.model;

import com.spartaclub.orderplatform.domain.payment.domain.model.Payment;
import com.spartaclub.orderplatform.global.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "p_orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Order extends BaseEntity {

    @Id
    @UuidGenerator
    @Column(name = "orderId", nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID orderId;

    @Column(name = "userId", nullable = false)
    private Long userId;

    @Column(name = "storeId", nullable = false, columnDefinition = "uuid")
    private UUID storeId;

    @Column(name = "totalPrice", nullable = false)
    private Long totalPrice;              // 총 금액

    @Column(name = "productCount", nullable = false)
    private Integer productCount;         // 상품개수

    @Column(name = "createdId", columnDefinition = "uuid")
    private UUID createdId;               // 생성자 ID

    @Column(name = "modifiedId", columnDefinition = "uuid")
    private UUID modifiedId;              // 수정자 ID

    @Column(name = "deletedId", columnDefinition = "uuid")
    private UUID deletedId;               // 삭제자 ID

    @Column(name = "address", length = 255)
    private String address;               // 배송 주소

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 40)
    private OrderStatus status;           // 주문 상태

    @Column(name = "memo", length = 100)
    private String memo;                  // 요청사항

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private Payment payment;     // 결제 엔티티 연관관계 1:1
}

// TODO: 연관관계 설정