package com.spartaclub.orderplatform.domain.product.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductReviewResponseDto {

    private Integer rating;             // 리뷰 별점
    private String contents;            // 리뷰 내용
    private String nickName;            // 유저 별명
}
