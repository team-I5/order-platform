package com.spartaclub.orderplatform.domain.product.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "상품 옵션 아이템 응답 DTO")
public class ProductOptionItemResponseDto {

    @Schema(description = "옵션 이름", example = "치즈 추가")
    private String optionName;

    @Schema(description = "옵션 추가 가격", example = "500")
    private Long additionalPrice;
}
