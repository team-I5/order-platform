package com.spartaclub.orderplatform.domain.review.presentation.controller;

import com.spartaclub.orderplatform.domain.review.application.service.ReviewService;
import com.spartaclub.orderplatform.domain.review.presentation.dto.request.ReviewCreateRequestDto;
import com.spartaclub.orderplatform.domain.review.presentation.dto.request.ReviewSearchRequestDto;
import com.spartaclub.orderplatform.domain.review.presentation.dto.request.ReviewUpdateRequestDto;
import com.spartaclub.orderplatform.domain.review.presentation.dto.response.ReviewResponseDto;
import com.spartaclub.orderplatform.domain.review.presentation.dto.response.ReviewSearchResponseDto;
import com.spartaclub.orderplatform.global.application.security.UserDetailsImpl;
import com.spartaclub.orderplatform.global.presentation.dto.ApiResponse;
import com.spartaclub.orderplatform.user.domain.entity.User;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * Review 컨트롤러 클래스
 * 리뷰 관련 API Endpoint 제공
 *
 * @author 이준성
 * @date 2025-10-02
 */
@Slf4j(topic = "Review Controller")
@RestController // @ResponseBody + @Controller
@RequiredArgsConstructor // final 제어자가 붙은 필드에 대한 생성자 생성
@RequestMapping("/v1/reviews")
public class ReviewController {

    // 필드 선언
    private final ReviewService reviewService;

    // 리뷰 등록 API
    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponseDto>> createReview(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @Valid @RequestBody ReviewCreateRequestDto requestDto) {
        User user = userDetails.getUser();
        ReviewResponseDto responseDto = reviewService.createReview(user, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(responseDto));
    }

    // 리뷰 수정 API
    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponseDto>> updateReview(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable UUID reviewId,
        @Valid @RequestBody ReviewUpdateRequestDto requestDto) {
        User user = userDetails.getUser();
        ReviewResponseDto responseDto = reviewService.updateReview(user, reviewId, requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto));
    }

    // 리뷰 삭제 API
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable UUID reviewId) {
        User user = userDetails.getUser();
        reviewService.deleteReview(user, reviewId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success());
    }

    // 리뷰 목록 조회 API
    @GetMapping("/{review}")
    public ResponseEntity<ApiResponse<Page<ReviewSearchResponseDto>>> searchReview(
        @RequestBody ReviewSearchRequestDto dto,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(reviewService.searchReview(dto, userDetails.getUser())));
    }
}
