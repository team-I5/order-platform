package com.spartaclub.orderplatform.domain.review.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

/*
 * 리뷰 응답 DTO 클래스
 * 리뷰 작성 또는 수정 시 클라이언트에게 반환할 데이터
 *
 * @author 이준성
 * @date 2025-10-02(목)
 */
@Schema(description = "리뷰 생성 응답 정보")
@Getter
@AllArgsConstructor
public class ReviewResponseDto {

    // 리뷰 ID
    @Schema(description = "리뷰ID")
    private UUID reviewId;
    // 고객 ID
    @Schema(description = "고객ID")
    private Long userId;
    // 가게 ID
    @Schema(description = "음식점ID")
    private UUID storeId;
    // 주문 ID
    @Schema(description = "주문ID")
    private UUID orderId;
    // 메뉴 ID
    @Schema(description = "상품ID")
    private UUID productId;
    // 별점
    @Schema(description = "리뷰 별점")
    private Integer rating;
    // 리뷰내용
    @Schema(description = "리뷰 내용")
    private String contents;
}
