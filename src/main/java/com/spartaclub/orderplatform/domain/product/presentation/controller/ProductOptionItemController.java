package com.spartaclub.orderplatform.domain.product.presentation.controller;

import com.spartaclub.orderplatform.domain.product.application.service.ProductOptionItemService;
import com.spartaclub.orderplatform.domain.product.presentation.dto.ProductOptionItemRequestDto;
import com.spartaclub.orderplatform.domain.product.presentation.dto.ProductOptionItemResponseDto;
import com.spartaclub.orderplatform.global.presentation.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.spartaclub.orderplatform.global.application.security.SecurityUtils.getCurrentUserId;

@RestController
@RequestMapping("/v1/product-option-items")
@RequiredArgsConstructor
public class ProductOptionItemController {

    private final ProductOptionItemService service;

    /**
     * 상품 옵션 아이템 생성 API *
     */
    @Operation(summary = "상품 옵션 아이템 생성", description = "상품 옵션 그룹에 새로운 옵션 아이템을 추가합니다. (점주 전용)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "옵션 아이템 생성 성공",
                    content = @Content(schema = @Schema(implementation = ProductOptionItemResponseDto.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "상품 옵션 그룹 없음")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<ProductOptionItemResponseDto>> createProductOptionItem(
            @Valid @RequestBody ProductOptionItemRequestDto productOptionItemRequestDto
    ) {
        ProductOptionItemResponseDto createItem = service.createProductOptionItem(productOptionItemRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(createItem));
    }

    /**
     * 상품 웁션 아이템 수정 API *
     */
    @Operation(summary = "상품 옵션 아이템 수정", description = "기존 옵션 아이템 정보를 수정합니다. (점주 전용)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "옵션 아이템 수정 성공",
                    content = @Content(schema = @Schema(implementation = ProductOptionItemResponseDto.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "옵션 아이템 없음")
    })
    @PutMapping("/{itemId}")
    public ResponseEntity<ApiResponse<ProductOptionItemResponseDto>> updateProductOptionItem(
            @Parameter(description = "옵션 아이템 ID") @PathVariable UUID itemId,
            @Valid @RequestBody ProductOptionItemRequestDto productOptionItemRequestDto
    ) {
        ProductOptionItemResponseDto updateItem = service.updateProductOptionItem(itemId, productOptionItemRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(updateItem));
    }

    /**
     * 상품 옵션 아이템 삭제 API *
     */
    @Operation(summary = "상품 옵션 아이템 삭제", description = "기존 옵션 아이템을 삭제합니다. (점주 전용)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "옵션 아이템 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "옵션 아이템 없음")
    })
    @DeleteMapping("/{itemId}")
    public ResponseEntity<ApiResponse<Void>> deleteProductOptionItem(
            @Parameter(description = "옵션 아이템 ID") @PathVariable UUID itemId
    ) {
        Long userId = getCurrentUserId();
        service.deleteProductOptionItem(userId, itemId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success(null));
    }
}
