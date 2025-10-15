package com.spartaclub.orderplatform.domain.product.presentation.controller;

import com.spartaclub.orderplatform.domain.product.application.service.ProductService;
import com.spartaclub.orderplatform.domain.product.presentation.dto.*;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreSearchResponseDto;
import com.spartaclub.orderplatform.global.presentation.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

import static com.spartaclub.orderplatform.global.application.security.SecurityUtils.getCurrentUserId;

/**
 * 상품 Controller
 *
 * @author 류형선
 * @date 2025-10-11(토)
 */
@RestController
@RequestMapping("/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 상품 등록 API
    @PreAuthorize("hasRole('OWNER')")
    @PostMapping
    @Operation(summary = "상품 상세 조회", description = "상품 ID로 상품 상세 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<ProductResponseDto>> createProduct(@Valid @RequestBody ProductCreateRequestDto productCreateRequestDto) {
        Long userId = getCurrentUserId();
        ProductResponseDto responseDto = productService.createProduct(productCreateRequestDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(responseDto));
    }

    // 상품 수정 API
    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponseDto>> updateProduct(
            @PathVariable UUID productId,
            @Valid @RequestBody ProductUpdateRequestDto productUpdateRequestDto
    ) {
        ProductResponseDto responseDto = productService.updateProduct(productId, productUpdateRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto));
    }

    // 상품 삭제 API, 회원 연결 시 서비스 로직에 id 추가
    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable UUID productId
    ) {
        Long userId = getCurrentUserId();
        productService.deleteProduct(userId, productId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success(null));
    }

    // 상품 공개/숨김 설정 API
    @PreAuthorize("hasRole('OWNER')")
    @PatchMapping("/{productId}/visibility")
    public ResponseEntity<ApiResponse<ProductResponseDto>> updateProductVisibility(@PathVariable UUID productId) {
        ProductResponseDto requestDto = productService.updateProductVisibility(productId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(requestDto));
    }

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/add-group-to-product")
    public ResponseEntity<ApiResponse<Void>> addGroupToProduct(@RequestBody ProductAddOptionGroupsRequestDto productAddOptionGroupsRequestDto) {
        productService.addGroupToProduct(productAddOptionGroupsRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null));
    }

    // 상품 목록 조회 API
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDto<ProductResponseDto>>> getProductList(
            @RequestParam UUID storeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponseDto<ProductResponseDto> productList = productService.getProductList(storeId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(productList));
    }

    // 상품 상세 조회 API
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductDetailResponseDto>> getProductList(@PathVariable UUID productId) {
        ProductDetailResponseDto requestDto = productService.getProduct(productId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(requestDto));
    }


    // 상품 검색 API
    @GetMapping("/search-by-product-Name")
    public ResponseEntity<ApiResponse<Page<StoreSearchResponseDto>>> searchProductByProductName(
            @RequestParam String keyword,
            @RequestParam(required = false) UUID addressId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<StoreSearchResponseDto> stores = productService.getStoreListByProductNameAndAddressId(keyword, addressId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(stores));
    }

    // 상품 별 리뷰 조회
    @GetMapping("/{productId}/reviews")
    public ResponseEntity<ApiResponse<Page<ProductReviewResponstDto>>> getProductReviews(@PathVariable UUID productId) {

    }
}
