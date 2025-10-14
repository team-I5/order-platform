package com.spartaclub.orderplatform.domain.product.presentation.dto;

import com.spartaclub.orderplatform.domain.product.domain.entity.OptionGroupTag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductOptionGroupResponseDto {
    private UUID productOptionGroupId;
    private String optionGroupName;
    private OptionGroupTag tag;
    private Long minSelect;
    private Long maxSelect;
    private List<ProductOptionItemResponseDto> optionItems;
}
