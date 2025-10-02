package com.spartaclub.orderplatform.domain.product.dto;

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
    private String productName;

    @Positive(message = "가격은 0보다 커야 합니다.")
    private Integer price;

    @Size(max = 500)
    private String productDescription;
}
