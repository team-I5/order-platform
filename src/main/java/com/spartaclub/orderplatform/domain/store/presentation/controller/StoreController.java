package com.spartaclub.orderplatform.domain.store.presentation.controller;

import com.spartaclub.orderplatform.domain.store.application.service.StoreService;
import com.spartaclub.orderplatform.domain.store.presentation.dto.request.StoreSearchByCategoryRequestDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.request.StoreSearchByKeywordRequestDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.request.StoreSearchRequestDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreCategoryResponseDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreDetailResponseDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreSearchResponseDto;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.global.auth.UserDetailsImpl;
import com.spartaclub.orderplatform.global.presentation.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/v1/stores")
@RestController
@RequiredArgsConstructor
@Tag(name = "Store - Common", description = "음식점 API")
public class StoreController {

    private final StoreService storeService;

    // 음식점 목록 조회 API
    @Operation(summary = "음식점 목록 조회", description = "사용자의 권한에 따라 음식점의 목록을 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "음식점 목록 조회 성공", content = @Content(schema = @Schema(implementation = StoreSearchResponseDto.class)))
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'OWNER', 'MANAGER', 'MASTER')")
    public ResponseEntity<ApiResponse<Page<StoreSearchResponseDto>>> searchStore(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @ParameterObject StoreSearchRequestDto dto,
        @ParameterObject Pageable pageable

    ) {
        User user = userDetails.getUser();
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(storeService.searchStore(dto, user, pageable)));
    }

    // 음식점 상세 조회 API
    @Operation(summary = "음식점 상세 조회", description = "사용자의 권한에 따라 음식점의 상세 내용을 조회합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "음식점 상세 조회 성공", content = @Content(schema = @Schema(implementation = StoreDetailResponseDto.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한이 없습니다."),
    })
    @GetMapping("/search/{storeId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'OWNER', 'MANAGER', 'MASTER')")
    public ResponseEntity<ApiResponse<StoreDetailResponseDto>> searchStoreDetail(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable UUID storeId
    ) {
        User user = userDetails.getUser();
        return ResponseEntity.status(HttpStatus.OK)
            .body(
                ApiResponse.success(storeService.searchStoreDetail(storeId, user, user.getRole()))
            );
    }

    // 음식점 카테고리별 검색 API
    @Operation(summary = "음식점 카테고리별 검색", description = "해당 카테고리의 음식점 목록을 검색합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "카테고리로 음식점 검색 성공", content = @Content(schema = @Schema(implementation = StoreSearchResponseDto.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한이 없습니다."),
    })
    @GetMapping("/search-category")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'OWNER', 'MANAGER', 'MASTER')")
    public ResponseEntity<ApiResponse<Page<StoreSearchResponseDto>>> searchStoreCategory(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @ModelAttribute StoreSearchByCategoryRequestDto dto,
        @ParameterObject Pageable pageable
    ) {
        User user = userDetails.getUser();
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(storeService.searchStoreByCategory(dto, user, pageable)));
    }

    // 음식점 이름(키워드)으로 검색 API
    @Operation(summary = "음식점 키워드 검색", description = "해당 키워드가 들어간 음식점 목록을 검색합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "키워드로 음식점 검색 성공", content = @Content(schema = @Schema(implementation = StoreCategoryResponseDto.class)))
    @PreAuthorize("hasAnyRole('CUSTOMER', 'OWNER', 'MANAGER', 'MASTER')")
    @GetMapping("/search-store-name")
    public ResponseEntity<ApiResponse<Page<StoreSearchResponseDto>>> searchStoreListByKeyword(
        @ModelAttribute StoreSearchByKeywordRequestDto dto,
        @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(storeService.searchStoreListByKeyword(dto, pageable)));
    }
}