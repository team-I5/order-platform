package com.spartaclub.orderplatform.domain.product.presentation.controller;

import com.spartaclub.orderplatform.domain.product.application.service.ProductService;
import com.spartaclub.orderplatform.domain.product.presentation.dto.*;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreSearchResponseDto;
import com.spartaclub.orderplatform.global.presentation.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@RestController
@RequestMapping("/v1/products")
@RequiredArgsConstructor
@Tag(name = "상품 API", description = "상품 등록, 수정, 삭제, 조회 및 검색 관련 API")
public class ProductController {

    private final ProductService productService;

    /**
     * 상품 등록 API
     */
    @Operation(summary = "상품 등록", description = "점주 권한(OWNER)으로 새로운 상품을 등록합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201", description = "상품 등록 성공",
                    content = @Content(schema = @Schema(implementation = ProductResponseDto.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "상점 없음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PreAuthorize("hasRole('OWNER')")
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponseDto>> createProduct(
            @Valid @RequestBody ProductCreateRequestDto productCreateRequestDto
    ) {
        Long userId = getCurrentUserId();
        ProductResponseDto responseDto = productService.createProduct(productCreateRequestDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(responseDto));
    }

    /**
     * 상품 수정 API *
     */
    @Operation(summary = "상품 수정", description = "기존 상품 정보를 수정합니다. (점주 전용)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "상품 수정 성공",
                    content = @Content(schema = @Schema(implementation = ProductResponseDto.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "상품 없음")
    })
    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponseDto>> updateProduct(
            @Parameter(description = "상품 ID") @PathVariable UUID productId,
            @Valid @RequestBody ProductUpdateRequestDto productUpdateRequestDto
    ) {
        ProductResponseDto responseDto = productService.updateProduct(productId, productUpdateRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto));
    }

    /**
     * 상품 삭제 API, 회원 연결 시 서비스 로직에 id 추가 *
     */
    @Operation(summary = "상품 삭제", description = "상품을 삭제합니다. (Soft delete, 점주 전용)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "상품 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "상품 없음")
    })
    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @Parameter(description = "상품 ID") @PathVariable UUID productId
    ) {
        Long userId = getCurrentUserId();
        productService.deleteProduct(userId, productId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success(null));
    }

    /**
     * 상품 공개/숨김 설정 API *
     */
    @Operation(summary = "상품 공개/숨김 설정", description = "상품의 공개 상태를 토글합니다. (점주 전용)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "공개/숨김 상태 변경 성공",
                    content = @Content(schema = @Schema(implementation = ProductResponseDto.class))
            )
    })
    @PreAuthorize("hasRole('OWNER')")
    @PatchMapping("/{productId}/visibility")
    public ResponseEntity<ApiResponse<ProductResponseDto>> updateProductVisibility(
            @Parameter(description = "상품 ID") @PathVariable UUID productId
    ) {
        ProductResponseDto responseDto = productService.updateProductVisibility(productId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto));
    }

    /**
     * 상품에 옵션 그룹 추가 *
     */
    @Operation(summary = "상품에 옵션 그룹 추가", description = "상품에 기존 옵션 그룹을 연결합니다. (점주 전용)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "옵션 그룹 연결 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "옵션 그룹 또는 상품 없음")
    })
    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/add-group-to-product")
    public ResponseEntity<ApiResponse<Void>> addGroupToProduct(
            @Valid @RequestBody ProductAddOptionGroupsRequestDto productAddOptionGroupsRequestDto
    ) {
        productService.addGroupToProduct(productAddOptionGroupsRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null));
    }

    /**
     * 상품 목록 조회 API *
     */
    @Operation(summary = "상품 목록 조회", description = "특정 상점의 상품 목록을 페이지 단위로 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상품 목록 조회 성공")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponseDto<ProductResponseDto>>> getProductList(
            @Parameter(description = "상점 ID") @RequestParam UUID storeId,
            @Parameter(description = "페이지 번호 (기본 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기 (기본 10)") @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponseDto<ProductResponseDto> productList = productService.getProductList(storeId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(productList));
    }

    /**
     * 상품 상세 조회 API *
     */
    @Operation(summary = "상품 상세 조회", description = "상품 ID로 상품의 상세 정보를 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상품 상세 조회 성공")
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductDetailResponseDto>> getProduct(
            @Parameter(description = "상품 ID") @PathVariable UUID productId
    ) {
        ProductDetailResponseDto responseDto = productService.getProduct(productId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto));
    }

    /**
     * 상품 검색 API *
     */
    @Operation(summary = "상품 이름으로 상점 검색", description = "입력한 상품 이름과 사용자 주소를 기반으로 배송 가능한 상점을 검색합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상점 검색 성공")
    @GetMapping("/search-by-product-name")
    public ResponseEntity<ApiResponse<Page<StoreSearchResponseDto>>> searchProductByProductName(
            @Parameter(description = "검색 키워드 (상품 이름)") @RequestParam String keyword,
            @Parameter(description = "사용자 주소 ID") @RequestParam(required = false) UUID addressId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<StoreSearchResponseDto> stores = productService.getStoreListByProductNameAndAddressId(keyword, addressId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(stores));
    }

    /**
     * 상품 별 리뷰 조회 *
     */
    @Operation(summary = "상품 리뷰 조회", description = "상품에 등록된 리뷰 목록을 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "리뷰 조회 성공")
    @GetMapping("/{productId}/reviews")
    public ResponseEntity<ApiResponse<PageResponseDto<ProductReviewResponseDto>>> getProductReviews(
            @Parameter(description = "상품 ID") @PathVariable UUID productId,
            @Parameter(description = "페이지 번호 (기본 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기 (기본 10)") @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponseDto<ProductReviewResponseDto> reviews = productService.getReviewListByProductId(productId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(reviews));
    }
}
