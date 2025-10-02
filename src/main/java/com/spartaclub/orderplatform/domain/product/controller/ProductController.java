package com.spartaclub.orderplatform.domain.product.controller;

import com.spartaclub.orderplatform.domain.product.dto.ProductResponseDto;
import com.spartaclub.orderplatform.domain.product.dto.ProductCreateRequestDto;
import com.spartaclub.orderplatform.domain.product.dto.ProductUpdateRequestDto;
import com.spartaclub.orderplatform.domain.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 상품 Controller
 *
 * @author 류형선
 * @date 2025-10-01(수)
 */
@RestController
@RequestMapping("/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 상품 등록 API
    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody ProductCreateRequestDto productCreateRequestDto) {
        ProductResponseDto responseDto = productService.createProduct(productCreateRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // 상품 수정 API
    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable UUID productId,
            @Valid @RequestBody ProductUpdateRequestDto productUpdateRequestDto
    ) {
        ProductResponseDto responseDto = productService.updateProduct(productId, productUpdateRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    // 상품 삭제 API


    // 상품 목록 조회 API


    // 상품 상세 조회 API


    // 상품 공개/숨김 설정 API
}
