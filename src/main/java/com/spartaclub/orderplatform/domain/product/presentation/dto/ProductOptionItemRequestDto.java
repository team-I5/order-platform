package com.spartaclub.orderplatform.domain.product.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductOptionItemRequestDto {
    private UUID productOptionGroupId;
    private String optionName;
    private Long additionalPrice;
}
