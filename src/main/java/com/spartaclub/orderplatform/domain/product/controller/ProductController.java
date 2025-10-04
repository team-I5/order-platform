package com.spartaclub.orderplatform.domain.product.controller;

import com.spartaclub.orderplatform.domain.product.dto.PageResponseDto;
import com.spartaclub.orderplatform.domain.product.dto.ProductCreateRequestDto;
import com.spartaclub.orderplatform.domain.product.dto.ProductResponseDto;
import com.spartaclub.orderplatform.domain.product.dto.ProductUpdateRequestDto;
import com.spartaclub.orderplatform.domain.product.service.ProductService;
import com.spartaclub.orderplatform.global.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 상품 Controller
 *
 * @author 류형선
 * @date 2025-10-02(목)
 */
@RestController
@RequestMapping("/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 상품 등록 API
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponseDto>> createProduct(@Valid @RequestBody ProductCreateRequestDto productCreateRequestDto) {
        ProductResponseDto responseDto = productService.createProduct(productCreateRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(responseDto));
    }

    // 상품 수정 API
    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponseDto>> updateProduct(
            @PathVariable UUID productId,
            @Valid @RequestBody ProductUpdateRequestDto productUpdateRequestDto
    ) {
        ProductResponseDto responseDto = productService.updateProduct(productId, productUpdateRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto));
    }

    // 상품 삭제 API, 회원 연결 시 서비스 로직에 id 추가
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable UUID productId
//            @AuthenticationPrincipal Long userId
    ) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
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
    public ResponseEntity<ApiResponse<ProductResponseDto>> getProductList(@PathVariable UUID productId) {
        ProductResponseDto requestDto = productService.getProduct(productId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(requestDto));
    }

    // 상품 공개/숨김 설정 API
    @PatchMapping("/{productId}/visibility")
    public ResponseEntity<ApiResponse<ProductResponseDto>> updateProductVisibility(@PathVariable UUID productId) {
        ProductResponseDto requestDto = productService.updateProductVisibility(productId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(requestDto));
    }
}
