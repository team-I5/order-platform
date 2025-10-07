package com.spartaclub.orderplatform.domain.review.dto;

/*
 * 리뷰 요청 DTO 클래스
 *
 * @author 이준성
 * @date 2025-10-02(목)
 */

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class ReviewRequestDto {

    private UUID orderId;

    private Integer rating;

    private String contents;
}
