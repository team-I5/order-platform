package com.spartaclub.orderplatform.domain.review.controller;

import com.spartaclub.orderplatform.domain.review.dto.ReviewCreateRequestDto;
import com.spartaclub.orderplatform.domain.review.dto.ReviewResponseDto;
import com.spartaclub.orderplatform.domain.review.dto.ReviewUpdateRequestDto;
import com.spartaclub.orderplatform.domain.review.service.ReviewService;
import com.spartaclub.orderplatform.global.presentation.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/*
 * Review 컨트롤러 클래스
 * 리뷰 관련 API Endpoint 제공
 *
 * @author 이준성
 * @date 2025-10-02
 */
@RestController
@RequestMapping("/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {
    // 필드 선언
    private final ReviewService reviewService;

    // 리뷰 등록 API
    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponseDto>> createReview(@Valid @RequestBody ReviewCreateRequestDto reviewCreateRequestDto) {
        ReviewResponseDto reviewResponseDto = reviewService.createReview(reviewCreateRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(reviewResponseDto));
    }

    // 리뷰 수정 API
    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponseDto>> updateReview(@PathVariable UUID reviewId, @Valid @RequestBody ReviewUpdateRequestDto reviewUpdateRequestDto) {
        ReviewResponseDto reviewResponseDto = reviewService.updateReview(reviewId, reviewUpdateRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(reviewResponseDto));
    }

    // 리뷰 삭제 API
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable UUID reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }

    // 리뷰 조회 API

}
