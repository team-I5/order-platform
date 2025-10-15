package com.spartaclub.orderplatform.domain.store.presentation.dto.response;

import com.spartaclub.orderplatform.domain.category.entity.Category;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StoreCategoryResponseDto {

    private String storeName;
    private List<Category> categories;
}
