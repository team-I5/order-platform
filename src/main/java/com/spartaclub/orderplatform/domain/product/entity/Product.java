package com.spartaclub.orderplatform.domain.product.entity;

import com.spartaclub.orderplatform.global.domain.entity.BaseEntity;
import com.spartaclub.orderplatform.domain.product.dto.ProductUpdateRequestDto;
import jakarta.persistence.*;
import jakarta.validation.Valid;
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
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

    // 상품 ID
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID productId;

    // 상품명
    @Column(nullable = false, length = 100)
    private String productName;

    // 가격
    @Column(nullable = false)
    private Integer price;

    // 상품 설명
    @Column(length = 500)
    private String productDescription;

    // 상품 숨김 여부
    @Column(nullable = false)
    private Boolean isHidden = false;


    // 소속 가게
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "store_id", nullable = false)
//    private Store store;


    // 생성자 ID
    @CreatedBy
    @Column(updatable = false, nullable = false)
    private Long createdId;

    // 수정자 ID
    @LastModifiedBy
    private Long modifiedId;

    // 삭제자 ID
    private Long deletedId;

    // 상품 정보 수정 메소드
    public void updateProduct(@Valid ProductUpdateRequestDto productUpdateRequestDto) {
        this.productName = productUpdateRequestDto.getProductName();
        this.price = productUpdateRequestDto.getPrice();
        this.productDescription = productUpdateRequestDto.getProductDescription();
    }

    // 상품 삭제 메소드 (soft delete)
    public void deleteProduct(Long userId) {
        this.isHidden = true;
        this.deletedId = userId;
        delete();
    }

    // 상품 공개/숨김 여부 수정 메소드
    public void updateVisibility() {
        this.isHidden = true;
    }
}

