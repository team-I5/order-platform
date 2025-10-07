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

    private String message;
    private UUID reviewId;

    /*
     * 리뷰 작성 응답 생성 메서드
     *
     * @param reviewId 작성된 리뷰 ID
     * @return 리뷰 작성 응답 메시지
     */
    public static ReviewResponseDto write(UUID reviewId) {
        return new ReviewResponseDto("리뷰가 작성되었습니다.", reviewId);
    }
}
