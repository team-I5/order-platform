package com.spartaclub.orderplatform.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

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
    // 평점
    
    // 리뷰 수정 메서드
    public void updateReview(ReviewUpdateRequestDto reviewUpdateRequestDto) {
        this.rating = reviewUpdateRequestDto.getRating();
        this.contents = reviewUpdateRequestDto.getContents();
    }

    /*
     * 리뷰 작성 응답 생성 메서드
     *
     * @param reviewId 작성된 리뷰 ID
     * @return 리뷰 작성 응답 메시지
     */
//    public static ReviewResponseDto write(UUID reviewId) {
//        return new ReviewResponseDto("리뷰가 작성되었습니다.", reviewId);
//    }
}
