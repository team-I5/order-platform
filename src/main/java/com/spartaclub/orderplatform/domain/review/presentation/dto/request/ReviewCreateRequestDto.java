package com.spartaclub.orderplatform.domain.review.presentation.dto.request;

/*
 * 리뷰 등록 요청 DTO 클래스
 *
 * @author 이준성
 * @date 2025-10-02(목)
 */

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewCreateRequestDto {

    // 주문 Id for 중복체크
    @NotNull(message = "중복 체크를 위한 주문 ID 필요합니다.")
    private UUID orderId;
    // 별점
    @NotNull(message = "서비스를 평가해주세요.")
    private Integer rating;
    // 리뷰 내용
    @Size(max = 1000, message = "리뷰는 1000자를 초과할 수 없습니다.")
    private String contents;
}
