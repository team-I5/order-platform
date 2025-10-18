package com.spartaclub.orderplatform.domain.category.application.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.spartaclub.orderplatform.domain.category.application.mapper.CategoryMapper;
import com.spartaclub.orderplatform.domain.category.domain.model.Category;
import com.spartaclub.orderplatform.domain.category.infrastructure.repository.CategoryRepository;
import com.spartaclub.orderplatform.domain.category.presentation.dto.request.CategoryRequestDto;
import com.spartaclub.orderplatform.domain.category.presentation.dto.response.CategoryResponseDto;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private UUID categoryId;
    private User manager;
    private CategoryRequestDto requestDto;
    private CategoryResponseDto responseDto;
    private Category category;
    Pageable pageable;

    @BeforeEach
    void setUp() {
        manager = User.createManager("manager1", "manager1@test.com",
            "hashPassword", "manager1", "01012345678");
        categoryId = UUID.randomUUID();
        requestDto = CategoryRequestDto.of("양식");
        category = Category.of(requestDto.getName());
        responseDto = new CategoryResponseDto(categoryId, "양식");
        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("카테고리 생성 성공")
    void createCategory_success() {
        given(categoryRepository.save(any(Category.class))).willReturn(category);
        given(categoryMapper.toCategoryResponseDto(any(Category.class))).willReturn(responseDto);

        CategoryResponseDto rlt = categoryService.createCategory(manager, requestDto);

        Assertions.assertThat(rlt.getType()).isEqualTo("양식");
        verify(categoryRepository).save(any(Category.class));
        verify(categoryMapper).toCategoryResponseDto(any(Category.class));
    }

    @Test
    @DisplayName("카테고리 수정 성공")
    void updateCategory_success() {
        given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));
        given(categoryRepository.save(any(Category.class))).willReturn(category);
        given(categoryMapper.toCategoryResponseDto(category)).willReturn(responseDto);
        CategoryResponseDto rlt = categoryService.updateCategory(manager, categoryId, requestDto);
        Assertions.assertThat(rlt).isEqualTo(responseDto);
    }

    @Test
    @DisplayName("카테고리 삭제 성공")
    void deleteCategory_success() {
        given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));
        categoryService.deleteCategory(manager, categoryId);
        Assertions.assertThat(category.isDeleted()).isTrue();
        verify(categoryRepository).deleteById(categoryId);
    }

    @Test
    @DisplayName("카테고리 목록 조회 성공")
    void searchCategory_success() {
        Category category1 = Category.of("한식");
        Category category2 = Category.of("중식");
        ReflectionTestUtils.setField(category1, "createdId", 1L);
        ReflectionTestUtils.setField(category2, "createdId", 2L);
        Page<Category> categoryPage = new PageImpl<>(List.of(category1, category2), pageable, 2);
        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        Page<CategoryResponseDto> rlt = categoryService.searchCategoryList(pageable);
        Assertions.assertThat(rlt).isNotNull();
        Assertions.assertThat(rlt.getContent()).hasSize(2);
        verify(categoryRepository).findAll(pageable);
    }

    @Test
    @DisplayName("카테고리 상세 조회 성공")
    void searchCategoryDetail_success() {
        given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));
        given(categoryMapper.toCategoryResponseDto(category)).willReturn(responseDto);
        CategoryResponseDto rlt = categoryService.searchCategory(categoryId);
        Assertions.assertThat(rlt).isEqualTo(responseDto);
    }
}
