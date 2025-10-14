package com.spartaclub.orderplatform.domain.product.domain.entity;

import com.spartaclub.orderplatform.domain.product.presentation.dto.ProductOptionItemRequestDto;
import com.spartaclub.orderplatform.global.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
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
@Builder
@AllArgsConstructor
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

    // 상품 그룹 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_option_group_id", nullable = false)
    private ProductOptionGroup productOptionGroup;

    // 생성자 ID
    @CreatedBy
    @Column(updatable = false, nullable = false)
    private Long createdId;

    // 수정자 ID
    @LastModifiedBy
    private Long modifiedId;

    // 삭제자 ID
    private Long deletedId;

    public void updateOptionItem(ProductOptionItemRequestDto productOptionItemRequestDto) {
        this.optionName = productOptionItemRequestDto.getOptionName();
        this.additionalPrice = productOptionItemRequestDto.getAdditionalPrice();
    }

    public void deleteItem(Long userId) {
        deletedId = userId;
        delete();
    }
}
