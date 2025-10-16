package com.spartaclub.orderplatform.domain.product.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
/**
 * 상품 등록 요청 Dto
 *
 * @author 류형선
 * @date 2025-10-01(수)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateRequestDto {

    @NotNull(message = "스토어 ID는 필수입니다.")
    @Schema(description = "상품이 속한 스토어 ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID storeId;

    @NotBlank(message = "상품명은 필수입니다.")
    @Size(max = 100, message = "상품명은 100자를 초과할 수 없습니다.")
    @Schema(description = "상품명", example = "치즈버거 세트")
    private String productName;

    @NotNull(message = "가격은 필수입니다.")
    @Positive(message = "가격은 0보다 커야 합니다.")
    @Schema(description = "상품 가격", example = "12000")
    private Long price;

    @Size(max = 500, message = "상품 설명은 최대 500자까지 입력할 수 있습니다.")
    @Schema(description = "상품 설명", example = "맛있는 치즈버거와 감자튀김 세트")
    private String productDescription;

    @NotNull(message = "상품 노출 여부는 필수입니다.")
    @Schema(description = "상품 노출 여부", example = "true")
    private Boolean isHidden;
}
