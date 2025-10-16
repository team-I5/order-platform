package com.spartaclub.orderplatform.domain.category.domain.model;

import jakarta.persistence.AttributeConverter;

public class CategoryConverter implements AttributeConverter<CategoryType, String> {

    // enum을 DB에 어떤 값으로 넣을 것인지 정의
    @Override
    public String convertToDatabaseColumn(CategoryType categoryType) {
        if (categoryType == null) {
            return null;
        }
        return categoryType.getName();
    }

    // DB에서 읽힌 값에 따라 어떻게 enum과 매칭 시킬 것인지 정의
    @Override
    public CategoryType convertToEntityAttribute(String ab) {
        return CategoryType.getInstance(ab);
    }
}
