package com.spartaclub.orderplatform.domain.category.domain.entity;

import com.spartaclub.orderplatform.domain.category.domain.model.Category;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CategoryTest {

    @Test
    @DisplayName("카테고리 객체 생성 with 정적 팩터리 메서드")
    void createCategory_success() {
        // given
        String type = "분식";
        // when
        Category category = Category.of(type);
        // then
        Assertions.assertEquals(type, category.getType());
    }
}
