package com.spartaclub.orderplatform.domain.product.presentation.dto;

import com.spartaclub.orderplatform.domain.product.domain.entity.OptionGroupTag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductOptionGroupRequestDto {

    @NotBlank(message = "옵션 그룹 이름은 필수입니다.")
    private String optionGroupName;

    @NotNull(message = "옵션 그룹 태그는 필수입니다.")
    private OptionGroupTag tag;

    @NotNull(message = "최소 선택 수는 필수입니다.")
    @Min(value = 0, message = "최소 선택 수는 0 이상이어야 합니다.")
    private Long minSelect;

    @NotNull(message = "최대 선택 수는 필수입니다.")
    @Min(value = 1, message = "최대 선택 수는 1 이상이어야 합니다.")
    private Long maxSelect;
}
