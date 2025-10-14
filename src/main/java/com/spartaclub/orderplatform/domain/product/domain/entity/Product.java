package com.spartaclub.orderplatform.domain.product.domain.entity;

import com.spartaclub.orderplatform.domain.product.presentation.dto.ProductUpdateRequestDto;
import com.spartaclub.orderplatform.domain.review.entity.Review;
import com.spartaclub.orderplatform.domain.store.domain.model.Store;
import com.spartaclub.orderplatform.global.domain.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

/**
 * 상품 정보 Entity
 *
 * @author 류형선
 * @date 2025-10-05(일)
 */
@Entity
@Table(name = "p_products")
@Getter
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
    private Long price;

    // 상품 설명
    @Column(length = 500)
    private String productDescription;

    // 상품 숨김 여부
    @Column(nullable = false)
    private Boolean isHidden = false;

    // 소속 가게
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    // 상품 주문 중간 테이블
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductOptionMap> productOptionGroupMaps = new ArrayList<>();

    // 메뉴 리뷰
    @OneToMany(mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    // 생성자 ID
    @CreatedBy
    @Column(updatable = false, nullable = false)
    private Long createdId;

    // 수정자 ID
    @LastModifiedBy
    private Long modifiedId;

    // 삭제자 ID
    private Long deletedId;

    // 정적 팩토리 메소드
    public static Product create(String productName, Long price, String description) {
        Product product = new Product();
        product.productName = productName;
        product.price = price;
        product.productDescription = description;
        return product;
    }

    // 상품 정보 수정 메소드
    public void updateProduct(@Valid ProductUpdateRequestDto productUpdateRequestDto) {
        this.productName = productUpdateRequestDto.getProductName();
        this.price = productUpdateRequestDto.getPrice();
        this.productDescription = productUpdateRequestDto.getProductDescription();
    }

    // 상품 삭제 메소드 (soft deleteProductOptionGroup)
    public void deleteProduct(Long userId) {
        this.isHidden = true;
        this.deletedId = userId;
        delete();
    }

    // 상품 공개/숨김 여부 수정 메소드
    public void updateVisibility() {
        this.isHidden = true;
    }

    // 상품과 옵션그룹 매핑
    public void addOptionGroup(ProductOptionGroup productOptionGroup) {
        ProductOptionMap map = ProductOptionMap.create(this, productOptionGroup);
        productOptionGroupMaps.add(map);
    }
}

