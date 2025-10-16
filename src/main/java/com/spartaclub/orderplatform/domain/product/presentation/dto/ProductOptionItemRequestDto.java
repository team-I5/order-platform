package com.spartaclub.orderplatform.domain.product.presentation.dto;

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
public class ProductOptionItemRequestDto {

    @NotNull(message = "상품 옵션 그룹 ID는 필수입니다.")
    private UUID productOptionGroupId;

    @NotBlank(message = "옵션 이름은 필수입니다.")
    private String optionName;

    @NotNull(message = "추가 가격은 필수입니다.")
    @Min(value = 0, message = "추가 가격은 0 이상이어야 합니다.")
    private Long additionalPrice;
}
