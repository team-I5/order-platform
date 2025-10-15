package com.spartaclub.orderplatform.domain.product.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductReviewResponseDto {

    private Integer rating;             // 리뷰 별점
    private String contents;            // 리뷰 내용
    private String username;            // 유저 이름
    private String nickName;            // 유저 별명
    private LocalDateTime createdAt;    // 생성 날짜
    private LocalDateTime modifiedAt;   // 수정 날짜
}
