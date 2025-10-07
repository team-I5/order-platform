package com.spartaclub.orderplatform.domain.review.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
 * 리뷰 수정 요청 Dto 클래스
 *
 * @author 이준성
 * @date 2025-10-07(화)
 */
@Getter
@NoArgsConstructor
public class ReviewUpdateRequestDto {
    // 별점
    @NotNull(message = "서비스를 평가해주세요.")
    private Integer rating;
    // 리뷰 내용
    @Size(max = 1000, message = "리뷰는 1000자를 초과할 수 없습니다.")
    private String contents;
}
