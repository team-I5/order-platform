package com.spartaclub.orderplatform.domain.product.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "상품 상세 응답 DTO")
public class ProductDetailResponseDto {

    @Schema(description = "상품 ID", example = "f2f57a3e-f9ec-4e70-96b8-de91c30b116d")
    private UUID productId;

    @Schema(description = "상품 이름", example = "불고기 버거")
    private String productName;

    @Schema(description = "상품 설명", example = "달콤한 불고기와 신선한 채소가 들어간 버거")
    private String productDescription;

    @Schema(description = "상품 가격", example = "8500")
    private Long price;

    @Schema(description = "상품에 포함된 옵션 그룹 목록")
    private List<ProductOptionGroupResponseDto> productOptionGroups;
}
