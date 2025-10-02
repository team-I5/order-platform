package com.spartaclub.orderplatform.domain.product.controller;

import com.spartaclub.orderplatform.domain.product.dto.ProductRequestDto;
import com.spartaclub.orderplatform.domain.product.dto.ProductResponseDto;
import com.spartaclub.orderplatform.domain.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody ProductRequestDto productRequestDto) {

        // 상품 생성
        ProductResponseDto responseDto = productService.createProduct(productRequestDto);

        // 성공 시 201과 데이터 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

}
