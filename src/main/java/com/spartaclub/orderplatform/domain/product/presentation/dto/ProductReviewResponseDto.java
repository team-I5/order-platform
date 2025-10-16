package com.spartaclub.orderplatform.domain.product.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "상품 리뷰 응답 DTO")
public class ProductReviewResponseDto {

    @Schema(description = "리뷰 별점", example = "5")
    private Integer rating;

    @Schema(description = "리뷰 내용", example = "정말 맛있어요!")
    private String contents;

    @Schema(description = "유저 이름", example = "hyunsun")
    private String username;

    @Schema(description = "유저 별명", example = "형선")
    private String nickName;

    @Schema(description = "리뷰 생성 날짜", example = "2025-10-16T12:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "리뷰 수정 날짜", example = "2025-10-16T12:30:00")
    private LocalDateTime modifiedAt;
}
