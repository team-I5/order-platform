package com.spartaclub.orderplatform.domain.product.presentation.dto;

import com.spartaclub.orderplatform.domain.product.domain.entity.OptionGroupTag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "상품 옵션 그룹 응답 DTO")
public class ProductOptionGroupResponseDto {

    @Schema(description = "상품 옵션 그룹 ID", example = "f2f57a3e-f9ec-4e70-96b8-de91c30b116d")
    private UUID productOptionGroupId;

    @Schema(description = "옵션 그룹 이름", example = "토핑 선택")
    private String optionGroupName;

    @Schema(description = "옵션 그룹 태그", example = "TOPPING")
    private OptionGroupTag tag;

    @Schema(description = "최소 선택 수", example = "0")
    private Long minSelect;

    @Schema(description = "최대 선택 수", example = "3")
    private Long maxSelect;

    @Schema(description = "옵션 그룹에 속한 옵션 아이템 목록")
    private Set<ProductOptionItemResponseDto> optionItems;
}
