package com.spartaclub.orderplatform.domain.product.presentation.dto;

import com.spartaclub.orderplatform.domain.product.domain.entity.OptionGroupTag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductOptionGroupRequestDto {
    private String optionGroupName;
    private OptionGroupTag tag;
    private Long minSelect;
    private Long maxSelect;
    private List<ProductOptionItemRequestDto> optionItems;
}
