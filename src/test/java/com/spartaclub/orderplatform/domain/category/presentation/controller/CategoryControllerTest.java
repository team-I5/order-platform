package com.spartaclub.orderplatform.domain.category.presentation.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spartaclub.orderplatform.domain.category.application.service.CategoryService;
import com.spartaclub.orderplatform.domain.category.presentation.dto.request.CategoryRequestDto;
import com.spartaclub.orderplatform.domain.category.presentation.dto.response.CategoryResponseDto;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.domain.user.domain.entity.UserRole;
import com.spartaclub.orderplatform.global.auth.UserDetailsImpl;
import com.spartaclub.orderplatform.global.presentation.dto.ApiResponse;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CategoryController.class)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoryService categoryService;

    private CategoryController categoryController;

    private User manager1;

    Pageable pageable = PageRequest.of(0, 10);

    @BeforeEach
    void setUp() {
        categoryController = new CategoryController(categoryService);

        manager1 = User.createUser(
            "manager1",
            "manager1@test.com",
            "Hashpassword3",
            "매니저1",
            "01087654321",
            UserRole.MASTER
        );
    }

    @Test
    @WithMockUser(roles = {"MASTER"})
    @DisplayName("카테고리 생성 API - 성공")
    public void createCategory_success() {
        UserDetailsImpl userDetails = new UserDetailsImpl(manager1);
        CategoryRequestDto requestDto = CategoryRequestDto.of("일식");

        CategoryResponseDto responseDto = new CategoryResponseDto(
            UUID.randomUUID(), requestDto.getName());
        when(categoryService.createCategory(manager1, requestDto)).thenReturn(responseDto);
        ResponseEntity<ApiResponse<CategoryResponseDto>> rlt =
            categoryController.createCategory(userDetails, requestDto);
        Assertions.assertEquals(HttpStatus.CREATED, rlt.getStatusCode());
        Assertions.assertNotNull(rlt.getBody());
        Assertions.assertEquals(responseDto.getType(), rlt.getBody().getData().getType());
    }

    @Test
    @WithMockUser(roles = {"MASTER"})
    @DisplayName("카테고리 수정 API - 성공")
    public void updateCategory_success() {
        UserDetailsImpl userDetails = new UserDetailsImpl(manager1);
        CategoryRequestDto requestDto = CategoryRequestDto.of("중식");
        UUID categoryId = UUID.randomUUID();
        CategoryResponseDto responseDto = new CategoryResponseDto(
            categoryId, requestDto.getName()
        );
        when(categoryService.updateCategory(userDetails.getUser(), categoryId,
            requestDto)).thenReturn(responseDto);
        ResponseEntity<ApiResponse<CategoryResponseDto>> rlt =
            categoryController.updateCategory(userDetails, categoryId, requestDto);
        Assertions.assertEquals(HttpStatus.OK, rlt.getStatusCode());
        Assertions.assertNotNull(rlt.getBody());
        Assertions.assertEquals(responseDto.getType(), rlt.getBody().getData().getType());
    }

    @Test
    @WithMockUser(roles = {"MASTER"})
    @DisplayName("카테고리 삭제 성공")
    public void deleteCategory_success() {
        UserDetailsImpl userDetails = new UserDetailsImpl(manager1);
        UUID categoryId = UUID.randomUUID();
        doNothing().when(categoryService).deleteCategory(manager1, categoryId);
        ResponseEntity<ApiResponse<Void>> rlt =
            categoryController.deleteCategory(userDetails, categoryId);
        Assertions.assertEquals(HttpStatus.NO_CONTENT, rlt.getStatusCode());
        verify(categoryService).deleteCategory(manager1, categoryId);
    }

    @Test
    @DisplayName("카테고리 목록 조회")
    public void getCategory_success() {
        List<CategoryResponseDto> mockList = List.of(
            new CategoryResponseDto(UUID.randomUUID(), "한식"),
            new CategoryResponseDto(UUID.randomUUID(), "양식")
        );
        Page<CategoryResponseDto> categoryList = new PageImpl<>(mockList, pageable, 10);
        when(categoryService.searchCategoryList(pageable)).thenReturn(categoryList);
        ResponseEntity<ApiResponse<Page<CategoryResponseDto>>> rlt =
            categoryController.searchCategoryList(pageable);
        Assertions.assertEquals(HttpStatus.OK, rlt.getStatusCode());
        Assertions.assertNotNull(rlt.getBody());
        Assertions.assertEquals(categoryList.stream().count(),
            rlt.getBody().getData().stream().count());
    }

    @Test
    @DisplayName("카테고리 상세 조회")
    public void searchCategoryDetail_success() {
        UUID categoryId = UUID.randomUUID();
        CategoryResponseDto responseDto = new CategoryResponseDto(categoryId, "중식");
        when(categoryService.searchCategory(categoryId)).thenReturn(responseDto);
        ResponseEntity<ApiResponse<CategoryResponseDto>> rlt =
            categoryController.searchCategory(categoryId);
        Assertions.assertEquals(HttpStatus.OK, rlt.getStatusCode());
        Assertions.assertNotNull(rlt.getBody());
        Assertions.assertEquals(responseDto.getType(), rlt.getBody().getData().getType());
    }

}
