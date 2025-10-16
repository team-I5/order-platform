package com.spartaclub.orderplatform.domain.product.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "상품 옵션 아이템 요청 DTO")
public class ProductOptionItemRequestDto {

    @Schema(description = "상품 옵션 그룹 ID", example = "f2f57a3e-f9ec-4e70-96b8-de91c30b116d")
    @NotNull(message = "상품 옵션 그룹 ID는 필수입니다.")
    private UUID productOptionGroupId;

    @Schema(description = "옵션 이름", example = "치즈 추가")
    @NotBlank(message = "옵션 이름은 필수입니다.")
    private String optionName;

    @Schema(description = "옵션 추가 가격", example = "500")
    @NotNull(message = "추가 가격은 필수입니다.")
    @Min(value = 0, message = "추가 가격은 0 이상이어야 합니다.")
    private Long additionalPrice;
}
