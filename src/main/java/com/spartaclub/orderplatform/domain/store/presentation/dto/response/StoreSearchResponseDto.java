package com.spartaclub.orderplatform.domain.store.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StoreSearchResponseDto {

    private String storeName;
    private Double averageRating;
    private Integer reviewCount;
}
