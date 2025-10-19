package com.spartaclub.orderplatform.domain.review.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "리뷰 수정 응답 정보")
@Getter
@AllArgsConstructor
public class ReviewSearchResponseDto {

    @Schema(description = "리뷰 ID")
    private UUID reviewId;
    @Schema(description = "리뷰 별점")
    private Integer rating;
    @Schema(description = "리뷰 내용")
    private String contents;
    @Schema(description = "가게 이름")
    private String storeName;
}
