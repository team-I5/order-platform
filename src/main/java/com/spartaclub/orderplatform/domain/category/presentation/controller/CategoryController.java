package com.spartaclub.orderplatform.domain.category.presentation.controller;

import com.spartaclub.orderplatform.domain.category.application.service.CategoryService;
import com.spartaclub.orderplatform.domain.category.presentation.dto.request.CategoryRequestDto;
import com.spartaclub.orderplatform.domain.category.presentation.dto.response.CategoryResponseDto;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.global.auth.UserDetailsImpl;
import com.spartaclub.orderplatform.global.presentation.dto.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * Cateogory 컨트롤러 클래스
 * 카테고리 관련 API Endpoint 제공
 *
 * @author 이준성
 * @date 2025-10-02
 */
@Slf4j(topic = "Category Control")
@RestController
@RequestMapping("/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    // 필드 선언
    private final CategoryService categoryService;

    // 카테고리 등록 API
    @PreAuthorize("hasAnyRole({'MASTER','MANAGER'})")
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponseDto>> createCategory(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @Valid @RequestBody CategoryRequestDto dto) {
        User user = userDetails.getUser();
        CategoryResponseDto responseDto = categoryService.createCategory(user, dto);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(responseDto));
    }

    // 카테고리 수정 API
    @PreAuthorize("hasAnyRole({'MASTER','MANAGER'})")
    @PatchMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponseDto>> updateCategory(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable UUID categoryId,
        @Valid @RequestBody CategoryRequestDto dto) {
        User user = userDetails.getUser();
        CategoryResponseDto responseDto = categoryService.updateCategory(user, categoryId, dto);
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(responseDto));
    }

    // 카테고리 삭제 API
    @PreAuthorize("hasAnyRole({'MASTER','MANAGER'})")
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable UUID categoryId) {
        User user = userDetails.getUser();
        categoryService.deleteCategory(user, categoryId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .body(ApiResponse.success());
    }

    // 카테고리 상세 조회 API
    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponseDto>> searchCategory(
        @PathVariable UUID categoryId
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(categoryService.searchCategory(categoryId)));
    }

    // 카테고리 상세 조회 API
    @PreAuthorize("hasAnyRole({'MASTER','MANAGER'})")
    @GetMapping("")
    public ResponseEntity<ApiResponse<List<CategoryResponseDto>>> searchCategoryList(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        Pageable pageable
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(categoryService.searchCategoryList(userDetails.getUser())));
    }
}
