package com.spartaclub.orderplatform.domain.order.domain.model;

import com.spartaclub.orderplatform.domain.payment.domain.model.Payment;
import com.spartaclub.orderplatform.domain.store.entity.Store;
import com.spartaclub.orderplatform.domain.user.entity.User;
import com.spartaclub.orderplatform.global.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "p_orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@AllArgsConstructor
public class Order extends BaseEntity {

    @Id
    @UuidGenerator
    @Column(name = "orderId", nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private User userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store storeId;

    @OneToMany(mappedBy = "order",
        cascade = CascadeType.ALL,        // Order 저장/삭제 시 자식도 같이
        orphanRemoval = true,             // 컬렉션에서 제거 시 DB에서도 삭제
        fetch = FetchType.LAZY)
    private List<OrderProduct> orderProducts = new ArrayList<>();

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
    // TODO: 주소 엔티티 만들어지면 연관관계 설정하기

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 40)
    private OrderStatus status;           // 주문 상태

    @Column(name = "memo", length = 100)
    private String memo;                  // 요청사항

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private Payment payment;     // 결제 엔티티 연관관계 1:1
}

// TODO: 연관관계 설정