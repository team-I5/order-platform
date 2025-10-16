package com.spartaclub.orderplatform.domain.order.domain.model;

import com.spartaclub.orderplatform.domain.product.domain.entity.Product;
import com.spartaclub.orderplatform.global.domain.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "p_orders_products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderProduct extends BaseEntity {

    @Id
    @UuidGenerator
    @Column(name = "order_product_id", nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID orderProductId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, columnDefinition = "uuid")
    private Order order;                   // 주문 FK

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false, columnDefinition = "uuid")
    private Product product;                 // 상품 FK

    @Column(name = "quantity", nullable = false)
    private Integer quantity;               // 수량

    @Column(name = "unitPrice", nullable = false)
    private Long unitPrice;           // 주문 시점 개당 가격

    @Column(name = "productName", nullable = false, length = 200)
    private String productName;             // 주문 시점 메뉴명

    OrderProduct(Product product, String productName, Long unitPrice, Integer quantity,
        Order order) {
        this.product = product;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.order = order;
    }

    // 정적 팩토리 메서드
    public static OrderProduct of(Product product, int quantity, Order order) {
        return new OrderProduct(
            product,
            product.getProductName(),
            product.getPrice(),
            quantity,
            order
        );
    }
}