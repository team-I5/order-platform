package com.spartaclub.orderplatform.domain.product.presentation.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

/**
 * 상품에 옵션 그룹을 한 번에 연결하기 위한 요청 DTO
 */
@Getter
@Setter
public class ProductAddOptionGroupsRequestDto {

    @NotNull(message = "상품 ID는 필수입니다.")
    private UUID productId;   // 연결할 상품 ID

    @NotEmpty(message = "연결할 옵션 그룹 ID 목록은 비어 있을 수 없습니다.")
    private List<@NotNull(message = "옵션 그룹 ID는 null일 수 없습니다.") UUID> productOptionGroupIds; // 연결할 옵션 그룹 ID 목록
}
