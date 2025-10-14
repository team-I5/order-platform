package com.spartaclub.orderplatform.domain.product.domain.entity;

import com.spartaclub.orderplatform.domain.product.presentation.dto.ProductOptionGroupRequestDto;
import com.spartaclub.orderplatform.global.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.util.*;

/**
 * 상품 옵션 그룹 Entity
 */
@Entity
@Table(name = "p_product_option_groups")
@Getter
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
    private Long minSelect = 0L;

    @Column(nullable = false)
    private Long maxSelect = Long.MAX_VALUE;

    @OneToMany(mappedBy = "productOptionGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductOptionMap> productOptionGroupMaps = new ArrayList<>();

    @OneToMany(mappedBy = "productOptionGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductOptionItem> optionItems = new HashSet<>();

    @CreatedBy
    @Column(updatable = false, nullable = false)
    private Long createdId;

    @LastModifiedBy
    private Long modifiedId;

    private Long deletedId;

    // 정적 팩토리 메소드
    public static ProductOptionGroup create(String optionGroupName, OptionGroupTag tag, Long minSelect, Long maxSelect) {
        ProductOptionGroup productOptionGroup = new ProductOptionGroup();
        productOptionGroup.optionGroupName = optionGroupName;
        productOptionGroup.tag = tag;
        productOptionGroup.minSelect = minSelect;
        productOptionGroup.maxSelect = maxSelect;
        return productOptionGroup;
    }

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
