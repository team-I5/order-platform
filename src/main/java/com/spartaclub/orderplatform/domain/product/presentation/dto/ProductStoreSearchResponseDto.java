package com.spartaclub.orderplatform.domain.product.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductStoreSearchResponseDto {
    private String storeName;
    private Double averageRating;
    private Integer reviewCount;
}
