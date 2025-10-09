package com.spartaclub.orderplatform.domain.product.domain.entity;

import com.spartaclub.orderplatform.domain.product.presentation.dto.ProductUpdateRequestDto;
import com.spartaclub.orderplatform.domain.store.domain.model.Store;
import com.spartaclub.orderplatform.global.domain.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

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
    private Long price;

    // 상품 설명
    @Column(length = 500)
    private String productDescription;

    // 상품 숨김 여부
    @Column(nullable = false)
    @Builder.Default
    private Boolean isHidden = false;


    // 소속 가게
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;


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

