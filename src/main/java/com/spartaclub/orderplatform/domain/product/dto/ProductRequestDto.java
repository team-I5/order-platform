package com.spartaclub.orderplatform.domain.product.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
/**
 * 상품 등록 요청 Dto
 *
 * @author 류형선
 * @date 2025-10-02(목)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDto {

    // 가게 ID
    @NotNull(message = "스토어 ID는 필수입니다.")
    private UUID storeId;

    // 상품 이름
    @NotBlank(message = "상품명은 필수입니다.")
    @Size(max = 100, message = "상품명은 100자를 초과할 수 없습니다.")
    private String productName;

    // 상품 가격
    @NotNull(message = "가격은 필수입니다.")
    @Positive(message = "가격은 0보다 커야 합니다.")
    private Integer price;

    // 상품 설명
    @Size(max = 500, message = "상품 설명은 최대 500자까지 입력할 수 있습니다.")
    private String productDescription;

    // 상품 숨김여부
    @NotNull(message = "상품 노출 여부는 필수입니다.")
    private Boolean isHidden;
}
