package com.spartaclub.orderplatform.domain.order.domain.model;

import com.spartaclub.orderplatform.domain.payment.domain.model.Payment;
import com.spartaclub.orderplatform.domain.store.domain.model.Store;
import com.spartaclub.orderplatform.global.domain.entity.BaseEntity;
import com.spartaclub.orderplatform.user.domain.entity.User;
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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    @Column(name = "order_id", nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Setter
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @OneToMany(mappedBy = "order",
        cascade = CascadeType.ALL,        // Order 저장/삭제 시 자식도 같이
        orphanRemoval = true,             // 컬렉션에서 제거 시 DB에서도 삭제
        fetch = FetchType.LAZY)
    @Builder.Default
    private List<OrderProduct> orderProducts = new ArrayList<>();

    @Column(name = "total_price", nullable = false)
    private Long totalPrice;              // 총 금액

    @Column(name = "product_count", nullable = false)
    private Integer productCount;         // 상품개수

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

    //주문 상품 추가(연관관계 형성)
    public void addOrderProduct(OrderProduct orderProduct) {
        this.orderProducts.add(orderProduct);
        orderProduct.setOrder(this);
    }

    //음식점 연관관계 형성
    public void setStore(Store store) {
        this.store = store;
        if (!store.getOrders().contains(this)) {
            store.getOrders().add(this);
        }
    }

    //주문 상태 변경
    public void changeStatus(OrderStatus orderStatus) {
        this.status = orderStatus;
    }

    //주문 상태 및 결제 금액 검증
    public void validatePaymentAvailable(Long requestAmount) {
        if (this.status != OrderStatus.PAYMENT_PENDING) {
            throw new IllegalStateException(
                "결제를 진행할 수 없는 주문 상태입니다. (현재 상태: " + this.status + ")"
            );
        }

        if (!Objects.equals(this.totalPrice, requestAmount)) {
            throw new IllegalStateException("결제 요청 금액이 주문 총액과 일치하지 않습니다. "
                + "(주문금액: " + this.totalPrice + ", 요청금액: " + requestAmount + ")");
        }
    }

    //주문 취소 가능 시간
    private static final long CANCEL_WINDOW_MINUTES = 5L;

    //주문 취소 가능 여부 검증
    public void checkCancelable() {
        // 상태 검증
        if (this.status != OrderStatus.PAYMENT_PENDING && this.status != OrderStatus.PAID) {
            throw new IllegalStateException(
                "해당 주문 상태에서는 취소할 수 없습니다. (현재 상태: " + this.status + ")"
            );
        }

        // 시간 검증 (주문 생성 후 5분 이내)
        LocalDateTime created = this.getCreatedAt();
        if (created == null) {
            throw new IllegalStateException("주문 생성 시각이 유효하지 않아 취소 검증을 수행할 수 없습니다.");
        }

        long elapsedMinutes = Duration.between(created, LocalDateTime.now()).toMinutes();
        if (elapsedMinutes > CANCEL_WINDOW_MINUTES) {
            throw new IllegalStateException(
                "주문 후 5분이 지나 취소할 수 없습니다. (경과: " + elapsedMinutes + "분)"
            );
        }
    }

    //주문 승인 가능 여부 검증
    public void checkAcceptable() {
        if (this.status != OrderStatus.PAID) {
            throw new IllegalStateException(
                "결제 완료 상태의 주문만 승인할 수 있습니다. (현재 상태: " + this.status + ")"
            );
        }
    }

    //주문 거부 가능 여부
    public void checkRejectable() {
        if (this.status != OrderStatus.PAYMENT_PENDING && this.status != OrderStatus.PAID) {
            throw new IllegalStateException(
                "결제 대기 또는 결제 완료 상태의 주문만 거절할 수 있습니다. (현재 상태: " + this.status + ")"
            );
        }
    }

    //주문 배달 완료 처리 가능 여부
    public void checkDeliverable() {
        if (this.status != OrderStatus.ACCEPTED) {
            throw new IllegalStateException(
                "승인 상태의 주문만 배달 완료할 수 있습니다. (현재 상태: " + this.status + ")"
            );
        }
    }
}
