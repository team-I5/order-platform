package com.spartaclub.orderplatform.domain.review.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
 * 리뷰 수정 요청 Dto 클래스
 *
 * @author 이준성
 * @date 2025-10-07(화)
 */
@Schema(description = "리뷰 수정 요청 정보")
@Getter
@NoArgsConstructor // 기본 생성자
@AllArgsConstructor // 값 수정 반영 위해 모든 필드 파라미터 생성자 적용
public class ReviewUpdateRequestDto {

    // 별점
    @Schema(description = "리뷰 별점(1~5점)", example = "1")
    @NotNull(message = "서비스를 평가해주세요.")
    private Integer rating;
    // 리뷰 내용
    @Schema(description = "리뷰 내용", example = "배달이 빨랐어요")
    @Size(min = 1, max = 1000, message = "키워드는 1자 이상 1000자 이내로 작성해주세요")
    @Size(max = 1000, message = "리뷰는 1000자를 초과할 수 없습니다.")
    private String contents;
}
