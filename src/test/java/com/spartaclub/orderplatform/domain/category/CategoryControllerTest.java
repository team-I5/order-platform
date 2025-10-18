package com.spartaclub.orderplatform.domain.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spartaclub.orderplatform.domain.category.application.service.CategoryService;
import com.spartaclub.orderplatform.domain.category.presentation.controller.CategoryController;
import com.spartaclub.orderplatform.domain.category.presentation.dto.request.CategoryRequestDto;
import com.spartaclub.orderplatform.domain.category.presentation.dto.response.CategoryResponseDto;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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

    @Test
    @DisplayName("카테고리 생성 API - 성공")
    @WithMockUser(roles={"MASTER"})
    void createCategory() throws Exception {
        // given
        CategoryRequestDto requestDto = new CategoryRequestDto("일식");
        CategoryResponseDto responseDto = new CategoryResponseDto(UUID.randomUUID(),"일식");

        when(categoryService.createCategory(User.class, CategoryRequestDto.class).getClass()

    }





}
