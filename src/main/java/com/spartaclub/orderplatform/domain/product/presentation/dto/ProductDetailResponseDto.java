package com.spartaclub.orderplatform.domain.product.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailResponseDto {
    private UUID productId;
    private String productName;
    private String productDescription;
    private Long price;
    private List<ProductOptionGroupResponseDto> productOptionGroups;
}
