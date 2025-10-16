package com.spartaclub.orderplatform.domain.product.presentation.controller;

import com.spartaclub.orderplatform.domain.product.application.service.ProductOptionGroupService;
import com.spartaclub.orderplatform.domain.product.presentation.dto.ProductOptionGroupRequestDto;
import com.spartaclub.orderplatform.domain.product.presentation.dto.ProductOptionGroupResponseDto;
import com.spartaclub.orderplatform.global.presentation.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.spartaclub.orderplatform.global.application.security.SecurityUtils.getCurrentUserId;

@RestController
@RequestMapping("/v1/product-option-groups")
@RequiredArgsConstructor
@Tag(name = "Product Option Group", description = "상품 옵션 그룹 관련 API")
public class ProductOptionGroupController {

    private final ProductOptionGroupService productOptionGroupService;

    /**
     * 상품 그룹 생성 API
     */
    @Operation(summary = "상품 옵션 그룹 생성", description = "새로운 상품 옵션 그룹을 생성합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상품 옵션 그룹 생성 성공",
                    content = @Content(schema = @Schema(implementation = ProductOptionGroupResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<ProductOptionGroupResponseDto>> createProductOptionGroup(
            @Valid @RequestBody ProductOptionGroupRequestDto productOptionGroupRequestDto) {
        ProductOptionGroupResponseDto createGroup = productOptionGroupService.createProductOptionGroup(productOptionGroupRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(createGroup));
    }

    /**
     * 상품 그룹 수정 API
     */
    @Operation(summary = "상품 옵션 그룹 수정", description = "기존 상품 옵션 그룹 정보를 수정합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상품 옵션 그룹 수정 성공",
                    content = @Content(schema = @Schema(implementation = ProductOptionGroupResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "옵션 그룹 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping("/{productOptionGroupId}")
    public ResponseEntity<ApiResponse<ProductOptionGroupResponseDto>> updateProductOptionGroup(
            @PathVariable UUID productOptionGroupId,
            @Valid @RequestBody ProductOptionGroupRequestDto productOptionGroupRequestDto) {
        ProductOptionGroupResponseDto updateGroup = productOptionGroupService.updateProductOptionGroup(productOptionGroupId, productOptionGroupRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(updateGroup));
    }

    /**
     * 상품 그룹 삭제 API
     */
    @Operation(summary = "상품 옵션 그룹 삭제", description = "기존 상품 옵션 그룹을 삭제합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "상품 옵션 그룹 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "옵션 그룹 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/{productOptionGroupId}")
    public ResponseEntity<ApiResponse<Void>> deleteProductOptionGroup(@PathVariable UUID productOptionGroupId) {
        Long userId = getCurrentUserId();
        productOptionGroupService.deleteProductOptionGroup(userId, productOptionGroupId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success());
    }

}
