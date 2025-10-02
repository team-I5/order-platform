package com.spartaclub.orderplatform.domain.product.entity;

import com.spartaclub.orderplatform.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.util.UUID;

/**
 * 상품 정보 Entity
 *
 * @author 류형선
 * @date 2025-10-02(목)
 */
@Entity
@Table(name = "p_products")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

    // 상품 ID
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "productId")
    private UUID productId;

    // 상품명
    @Column(name = "productName", nullable = false, length = 100)
    private String productName;

    // 가격
    @Column(name = "price", nullable = false)
    private Integer price;

    // 상품 설명
    @Column(name = "productDescription", length = 500)
    private String productDescription;

    // 상품 숨김 여부
    @Column(name = "isHidden", nullable = false)
    private Boolean isHidden = false;


    // 소속 가게
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "store_id", nullable = false)
//    private Store store;


    // 생성자ID
    @CreatedBy
    @Column(name = "createdId", updatable = false, nullable = false)
    private Long createdId;

    // 수정자ID
    @LastModifiedBy
    @Column(name = "modifiedId")
    private Long modifiedId;

    // 삭제자ID
    @Column(name = "deletedId")
    private Long deletedId;
}

