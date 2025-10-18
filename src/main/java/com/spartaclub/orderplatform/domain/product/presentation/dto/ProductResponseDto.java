package com.spartaclub.orderplatform.domain.product.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

/**
 * 상품 등록 응답 DTO
 *
 * @author 류형선
 * @date 2025-10-01(수)
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "상품 등록/조회 응답 DTO")
public class ProductResponseDto {

    @Schema(description = "상품 ID", example = "f2f57a3e-f9ec-4e70-96b8-de91c30b116d")
    private UUID productId;

    @Schema(description = "가게 ID", example = "66f654e2-8eed-4997-a889-6f4a577e088e")
    private UUID storeId;

    @Schema(description = "상품 이름", example = "불고기 버거")
    private String productName;

    @Schema(description = "상품 가격", example = "5500")
    private Integer price;

    @Schema(description = "상품 설명", example = "맛있는 불고기 패티와 신선한 야채로 만든 버거")
    private String productDescription;

    @Schema(description = "상품 숨김 여부", example = "false")
    private Boolean isHidden;
}
