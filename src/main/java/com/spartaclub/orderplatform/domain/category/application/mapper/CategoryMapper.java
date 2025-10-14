package com.spartaclub.orderplatform.domain.category.application.mapper;

import com.spartaclub.orderplatform.domain.category.domain.model.Category;
import com.spartaclub.orderplatform.domain.category.presentation.dto.request.CategoryRequestDto;
import com.spartaclub.orderplatform.domain.category.presentation.dto.response.CategoryResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
/*
 * 카테고리 Entity ↔ Dto 맵핑
 *
 * @author 이준성
 * @date 2025-10-13(월)
 */

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    // 외래키
    @Mapping(target = "storeCategories", ignore = true)
    // categoryId DB auto_increment로 자동 생성
    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "type", expression = "java(CategoryType.CHICKEN)")
    Category toCategoryEntity(CategoryRequestDto dto);

    //    @Mapping(target = "storeId", source = "store.storeId")
    @Mapping(target = "type", expression = "java(category.getType())")
    CategoryResponseDto toCategoryResponseDto(Category category);
}
