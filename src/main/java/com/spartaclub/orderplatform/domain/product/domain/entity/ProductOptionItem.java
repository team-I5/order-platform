package com.spartaclub.orderplatform.domain.product.domain.entity;

import com.spartaclub.orderplatform.domain.product.presentation.dto.ProductOptionItemRequestDto;
import com.spartaclub.orderplatform.global.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.util.UUID;

/**
 * 상품 옵션 Entity
 *
 * @author 류형선
 * @date 2025-10-12(일)
 */
@Entity
@Table(name = "p_product_option_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductOptionItem extends BaseEntity {

    // 상품 옵션 ID
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID productOptionItemId;

    // 옵션 이름
    @Column(nullable = false)
    private String optionName;

    // 추가 가격
    @Column(nullable = false)
    private Long additionalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_option_group_id", nullable = false)
    private ProductOptionGroup productOptionGroup;

    // 정적 팩토리 메소드
    public static ProductOptionItem create(ProductOptionGroup productOptionGroup, String optionName, Long additionalPrice) {
        ProductOptionItem productOptionItem = new ProductOptionItem();
        productOptionItem.productOptionGroup = productOptionGroup;
        productOptionItem.optionName = optionName;
        productOptionItem.additionalPrice = additionalPrice;
        return productOptionItem;
    }

    public void updateOptionItem(ProductOptionItemRequestDto productOptionItemRequestDto) {
        this.optionName = productOptionItemRequestDto.getOptionName();
        this.additionalPrice = productOptionItemRequestDto.getAdditionalPrice();
    }

    public void deleteItem(Long userId) {
        delete(userId);
    }
}
