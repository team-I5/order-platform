package com.spartaclub.orderplatform.domain.product.domain.entity;

import com.spartaclub.orderplatform.domain.product.presentation.dto.ProductOptionGroupRequestDto;
import com.spartaclub.orderplatform.global.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 상품 옵션 그룹 Entity
 */
@Entity
@Table(name = "p_product_option_groups")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductOptionGroup extends BaseEntity {

    // 상품 옵션 그룹 ID
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID productOptionGroupId;

    @Column(nullable = false, length = 100)
    private String optionGroupName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private OptionGroupTag tag;

    @Column(nullable = false)
    @Builder.Default
    private Long minSelect = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long maxSelect = Long.MAX_VALUE;

    @OneToMany(mappedBy = "productOptionGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductOptionMap> productOptionGroupMaps = new ArrayList<>();

    @OneToMany(mappedBy = "productOptionGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductOptionItem> optionItems = new ArrayList<>();

    @CreatedBy
    @Column(updatable = false, nullable = false)
    private Long createdId;

    @LastModifiedBy
    private Long modifiedId;

    private Long deletedId;

    public void updateOptionGroupInfo(ProductOptionGroupRequestDto requestDto) {
        this.optionGroupName = requestDto.getOptionGroupName();
        this.tag = requestDto.getTag();
        this.minSelect = requestDto.getMinSelect();
        this.maxSelect = requestDto.getMaxSelect();
    }

    public void deleteOptionGroup(Long userId) {
        this.deletedId = userId;
        delete();
    }
}
