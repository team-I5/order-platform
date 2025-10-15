package com.spartaclub.orderplatform.domain.product.presentation.dto;

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

    private UUID productId;                    // 연결할 상품 ID
    private List<UUID> productOptionGroupIds;         // 연결할 옵션 그룹 ID 목록
}
