package com.spartaclub.orderplatform.domain.store.presentation.dto.response;

import com.spartaclub.orderplatform.domain.category.domain.model.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class StoreSearchByCategoryResponseDto {

    private String storeName;
    private Double averageRating;
    private Integer reviewCount;
    private List<Category> categories;
}
