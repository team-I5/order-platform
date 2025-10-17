package com.spartaclub.orderplatform.domain.product.presentation.dto;

import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductStoreSearchResponseDto {
    private String storeName;
    private Double averageRating;
    private Integer reviewCount;
}
