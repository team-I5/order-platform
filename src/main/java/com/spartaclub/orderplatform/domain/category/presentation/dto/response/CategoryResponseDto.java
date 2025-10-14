package com.spartaclub.orderplatform.domain.category.presentation.dto.response;

import com.spartaclub.orderplatform.domain.category.domain.model.CategoryType;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
//@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseDto {

    //    // 카테고리 ID
    private UUID categoryId;
    // 가게 ID
//    private UUID storeId;
    // 카테고리명
    private CategoryType type;

}
