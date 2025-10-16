package com.spartaclub.orderplatform.domain.product.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 상품 수정 요청 Dto
 *
 * @author 류형선
 * @date 2025-10-02(목)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequestDto {

    @NotBlank(message = "상품명은 필수입니다.")
    @Size(max = 100)
    @Schema(description = "상품명", example = "치즈버거 세트")
    private String productName;

    @Positive(message = "가격은 0보다 커야 합니다.")
    @Schema(description = "상품 가격", example = "12000")
    private Long price;

    @Size(max = 500)
    @Schema(description = "상품 설명", example = "맛있는 치즈버거와 감자튀김 세트")
    private String productDescription;
}
