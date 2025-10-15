package com.spartaclub.orderplatform.domain.store.presentation.dto.response;

import com.spartaclub.orderplatform.domain.category.entity.Category;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StoreSearchByStoreNameResponseDto {

    private String storeName;
    private Double averageRating;
    private Integer reviewCount;
    private List<Category> categories;
}
