package com.spartaclub.orderplatform.domain.category.presentation.controller;

import com.spartaclub.orderplatform.domain.category.application.service.CategoryService;
import com.spartaclub.orderplatform.domain.category.presentation.dto.request.CategoryRequestDto;
import com.spartaclub.orderplatform.domain.category.presentation.dto.response.CategoryResponseDto;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.global.auth.UserDetailsImpl;
import com.spartaclub.orderplatform.global.presentation.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
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
 * 카테고리 컨트롤러 클래스
 * 카테고리 관련 API Endpoint 제공
 *
 * @author 이준성
 * @date 2025-10-02
 */
@Slf4j(topic = "Category Control")
@Tag(name = "Category", description = "카테고리 관리 API")
@RestController
@RequestMapping("/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    // 필드 선언
    private final CategoryService categoryService;

    /**
     * 카테고리 등록 API - 인증된 관리자 새로운 카테고리 등록
     *
     * @param userDetails 인증된 관리자 정보
     * @param requestDto  카테고리 등록 요청 데이터
     * @return 등록된 카테고리 정보
     */
    @Operation(summary = "카테고리 등록", description = "관리자가 카테고리를 등록합니다.")
    @PreAuthorize("hasAnyRole({'MASTER','MANAGER'})")
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponseDto>> createCategory(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @Valid @RequestBody CategoryRequestDto requestDto) {
        // 사용자 객체 받아옴
        User user = userDetails.getUser();
        // 카테고리 등록
        CategoryResponseDto responseDto = categoryService.
            createCategory(user, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(responseDto));
    }

    /**
     * 카테고리 수정 API - 인증된 관리자 기존 카테고리 수정
     *
     * @param userDetails 인증된 관리자 정보
     * @param categoryId  수정할 카테고리 ID
     * @param requestDto  카테고리 수정 요청 데이터
     * @return 수정된 카테고리 정보
     */
    @Operation(summary = "카테고리 수정", description = "관리자가 카테고리를 수정합니다.")
    @PreAuthorize("hasAnyRole({'MASTER','MANAGER'})")
    @PatchMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponseDto>> updateCategory(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable UUID categoryId,
        @Valid @RequestBody CategoryRequestDto requestDto) {
        // 사용자 객체 받아옴
        User user = userDetails.getUser();
        // 카테고리 수정
        CategoryResponseDto responseDto = categoryService.
            updateCategory(user, categoryId, requestDto);
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(responseDto));
    }

    /**
     * 카테고리 삭제 API - 인증된 관리자 기존 카테고리 삭제
     *
     * @param userDetails 인증된 관리자 정보
     * @param categoryId  삭제할 카테고리 ID
     * @return 응답 없음
     */
    @Operation(summary = "카테고리 삭제", description = "관리자가 카테고리를 삭제합니다.")
    @PreAuthorize("hasAnyRole({'MASTER','MANAGER'})")
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable UUID categoryId) {
        // 사용자 객체 받아옴
        User user = userDetails.getUser();
        // 카테고리 삭제
        categoryService.deleteCategory(user, categoryId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .body(ApiResponse.success());
    }

    /**
     * 카테고리 목록 조회 API - 카테고리 목록 조회합니다.
     *
     * @param pageable 페이징 처리
     * @return 카테고리 목록 정보
     */
    // 카테고리 목룍 조회 API
    @Operation(summary = "카테고리 목록 조회", description = "카테고리 목록 조회합니다.")
    @PreAuthorize("hasAnyRole({'MASTER','MANAGER'})")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CategoryResponseDto>>> searchCategoryList(
        @ParameterObject Pageable pageable) {
        List<CategoryResponseDto> categories = categoryService
            .searchCategoryList(pageable);
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(categories));
    }

    /**
     * 카테고리 상세 조회 API - 카테고리 하나 조회
     *
     * @param categoryId 조회할 카테고리 ID
     * @return 특정 카테고리 정보
     */
    @Operation(summary = "카테고리 상세 조회", description = "카테고리 하나 조회합니다.")
    @PreAuthorize("hasAnyRole({'MASTER','MANAGER'})")
    @GetMapping("/search/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponseDto>> searchCategory(
        @PathVariable UUID categoryId
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(categoryService.searchCategory(categoryId)));
    }
}
