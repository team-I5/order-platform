package com.spartaclub.orderplatform.domain.review.presentation.dto.response;

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
@Getter
@AllArgsConstructor
public class ReviewResponseDto {

    // 리뷰 ID
    private UUID reviewId;
    // 고객 ID
    private Long userId;
    // 가게 ID
    private UUID storeId;
    // 주문 ID
    private UUID orderId;
    // 메뉴 ID
    private UUID productId;
    // 별점
    private Integer rating;
    // 리뷰내용
    private String contents;
}
