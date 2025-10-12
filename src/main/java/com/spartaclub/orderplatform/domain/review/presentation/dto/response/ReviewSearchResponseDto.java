package com.spartaclub.orderplatform.domain.review.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewSearchResponseDto {
    private Integer rating;
    private String contents;
}
