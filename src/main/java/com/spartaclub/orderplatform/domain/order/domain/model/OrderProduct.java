package com.spartaclub.orderplatform.domain.order.domain.model;

import com.spartaclub.orderplatform.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "p_orders_products_seq")
    @SequenceGenerator(
        name = "p_orders_products_seq",  // JPA가 식별하기 위한 name
        sequenceName = "p_orders_products_seq", //데이터베이스에 저장되는 name
        allocationSize = 30  // Hibernate가 미리 확보해둘 PK 개수 메모리에 저장
    )
    @UuidGenerator
    @Column(name = "orderProductId", nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID orderProductId;

    @Column(name = "orderId", nullable = false, columnDefinition = "uuid")
    private UUID orderId;                   // 주문 FK

    @Column(name = "productId", nullable = false, columnDefinition = "uuid")
    private UUID productId;                 // 상품 FK

    @Column(name = "quantity", nullable = false)
    private Integer quantity;               // 수량

    @Column(name = "unitPrice", nullable = false)
    private Long unitPrice;           // 주문 시점 단가

    @Column(name = "productName", nullable = false, length = 200)
    private String productName;             // 주문 시점 메뉴명

    @Column(name = "createdId")
    private Long createdId;                 // 생성자 ID (BIGINT)

    @Column(name = "modifiedId")
    private Long modifiedId;                // 수정자 ID (BIGINT)

    @Column(name = "deletedId")
    private Long deletedId;                 // 삭제자 ID (BIGINT)

}

// TODO: 연관관계 설정