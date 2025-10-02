package com.spartaclub.orderplatform.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
/**
 * 상품 등록 응답 Dto
 *
 * @author 류형선
 * @date 2025-10-01(수)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto {

    // 상품 ID
    private UUID productId;
    // 가게 ID
    private UUID storeId;
    // 상품 이름
    private String productName;
    // 상품 가격
    private Integer price;
    // 상품 설명
    private String productDescription;
    // 상품 숨김여부
    private Boolean isHidden;

}
