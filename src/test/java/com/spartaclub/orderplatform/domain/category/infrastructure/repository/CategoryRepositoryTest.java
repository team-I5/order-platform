package com.spartaclub.orderplatform.domain.category.infrastructure.repository;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.spartaclub.orderplatform.domain.category.domain.model.Category;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class CategoryRepositoryTest {

    @Mock
    private CategoryRepository categoryRepository;

    Pageable pageable = PageRequest.of(0, 10);

    @Test
    @DisplayName("카테고리 목록 조회")
    void FindAllCategories() {
        // given
        Page<Category> dummyPage = new PageImpl<>(List.of(mock(Category.class)));
        given(categoryRepository.findAll(pageable)).willReturn(dummyPage);
        // when
        Page<Category> rlt = categoryRepository.findAll(pageable);
        // then
        Assertions.assertNotNull(rlt);
        Assertions.assertEquals(dummyPage.getTotalElements(), rlt.getSize());
        Assertions.assertEquals(dummyPage, rlt);
    }
}
