package com.spartaclub.orderplatform.domain.order.domain.model;

import com.spartaclub.orderplatform.domain.order.application.command.PlaceOrderCommand;
import com.spartaclub.orderplatform.domain.order.exception.OrderErrorCode;
import com.spartaclub.orderplatform.domain.payment.domain.model.Payment;
import com.spartaclub.orderplatform.domain.product.domain.entity.Product;
import com.spartaclub.orderplatform.domain.store.domain.model.Store;
import com.spartaclub.orderplatform.global.domain.entity.BaseEntity;
import com.spartaclub.orderplatform.global.exception.BusinessException;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.global.domain.entity.BaseEntity;
import com.spartaclub.orderplatform.global.exception.BusinessException;
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
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "p_orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Slf4j
public class Order extends BaseEntity {

    @Id
    @UuidGenerator
    @Column(name = "order_id", nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @OneToMany(mappedBy = "order",
        cascade = CascadeType.ALL,        // Order 저장/삭제 시 자식도 같이
        orphanRemoval = true,             // 컬렉션에서 제거 시 DB에서도 삭제
        fetch = FetchType.LAZY)
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
    }

    //주문 상태 변경
    public void changeStatus(OrderStatus orderStatus) {
        this.status = orderStatus;
    }

    //주문 상태 및 결제 금액 검증
    public void validatePaymentAvailable(Long requestAmount) {
        if (status.isNotPending()) {
            log.warn(
                "[Order-Validate] 주문 상태로 인한 결제 요청 또는 승인 불가 상태 감지 - requiredStatus={}, currentStatus={}",
                OrderStatus.PAYMENT_PENDING, status);
            throw new BusinessException(OrderErrorCode.INVALID_STATUS_FOR_PAYMENT);
        }

        if (!Objects.equals(this.totalPrice, requestAmount)) {
            throw new BusinessException(OrderErrorCode.PAYMENT_AMOUNT_MISMATCH);
        }
    }

    //주문 취소 가능 시간
    private static final long CANCEL_WINDOW_MINUTES = 5L;

    //주문 취소 가능 여부 검증
    public void checkCancelable() {
        // 상태 검증
        if (status.isNotPending() && status.isNotPaid()) {
            log.warn("[Order-Validate] 주문 취소 불가 상태 감지 - currentStatus={}", status);
            throw new BusinessException(OrderErrorCode.INVALID_STATUS_FOR_CANCELLATION);
        }

        // 시간 검증 (주문 생성 후 5분 이내)
        LocalDateTime created = this.getCreatedAt();
        if (created == null) {
            throw new BusinessException(OrderErrorCode.INVALID_CREATED_TIME);
        }

        long elapsedMinutes = Duration.between(created, LocalDateTime.now()).toMinutes();
        if (elapsedMinutes > CANCEL_WINDOW_MINUTES) {
            log.warn("[Order-Validate] 주문 취소 불가 상태 감지 - 주문 생성 후 경과 시간(분)={}", elapsedMinutes);
            throw new BusinessException(OrderErrorCode.CANCELLATION_WINDOW_EXPIRED);
        }
    }

    //주문 승인 가능 여부 검증
    public void checkAcceptable() {
        if (status.isNotPaid()) {
            log.warn("[Order-Validate] 주문 승인 불가 상태 감지 - requiredStatus={}, currentStatus={}",
                OrderStatus.PAID, status);
            throw new BusinessException(OrderErrorCode.INVALID_STATUS_FOR_ACCEPT);
        }
    }

    //주문 거부 가능 여부
    public void checkRejectable() {
        if (status.isNotPending() && status.isNotPaid()) {
            log.warn("[Order-Validate] 주문 거부 불가 상태 감지 - currentStatus={}", status);
            throw new BusinessException(OrderErrorCode.INVALID_STATUS_FOR_REJECT);
        }
    }

    //주문 배달 완료 처리 가능 여부
    public void checkDeliverable() {
        if (status.isNotAccepted()) {
            log.warn("[Order-Validate] 주문 배달 완료 처리 불가 상태 감지 - requiredStatus={}, currentStatus={}",
                OrderStatus.ACCEPTED, status);
            throw new BusinessException(OrderErrorCode.INVALID_STATUS_FOR_COMPLETE_DELIVERY);
        }
    }

    // User, Store 연관 관계 형성
    private void link(User user, Store store) {
        this.user = user;

        this.store = store;
        if (!store.getOrders().contains(this)) {
            store.getOrders().add(this);
        }
    }

    //주문 생성 정적 팩토리 메서드
    public static Order place(User user, Store store, List<PlaceOrderCommand> commands,
        Map<UUID, Product> products, String address, String memo) {

        Order order = new Order();
        order.link(user, store);
        order.status = OrderStatus.PAYMENT_PENDING;
        order.address = address;
        order.memo = memo;

        for (PlaceOrderCommand c : commands) {
            Product p = products.get(c.productId());
            OrderProduct op = OrderProduct.of(p, c.quantity(), order);

            order.addOrderProduct(op);
        }

        order.calculateTotalPriceAndCount();
        return order;
    }

    //주문 총액, 총 수량 계산
    private void calculateTotalPriceAndCount() {
        long sum = 0L;
        int cnt = 0;
        for (OrderProduct op : orderProducts) {
            sum += op.getUnitPrice() * (long) op.getQuantity();
            cnt += op.getQuantity();
        }
        this.totalPrice = sum;
        this.productCount = cnt;
    }
}
