package com.spartaclub.orderplatform.domain.category.infrastructure.repository;

import com.spartaclub.orderplatform.domain.category.TestAuditingConfig;
import com.spartaclub.orderplatform.domain.category.domain.model.Category;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@SpringBootTest
@Import(TestAuditingConfig.class)
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    Pageable pageable = PageRequest.of(0, 10);

    @Test
    @DisplayName("카테고리 목록 조회")
    void FindAllCategories() {
        // given
        Category category1 = Category.of("양식");
        Category category2 = Category.of("일식");

        categoryRepository.saveAll(List.of(category1, category2));
        // when
        Page<Category> rlt = categoryRepository.findAll(pageable);
        // then
        Assertions.assertNotNull(rlt);
        Assertions.assertEquals(2, rlt.getContent().size());
//        Assertions.assertTrue(rlt.getContent().stream()
//            .anyMatch(c -> c.getType().equals(category1.getType())));
//        Assertions.assertTrue(rlt.getContent().stream()
//            .anyMatch(c -> c.getType().equals(category2.getType())));
    }


}
