package com.spartaclub.orderplatform.domain.review.presentation.controller;

import com.spartaclub.orderplatform.domain.review.application.service.ReviewService;
import com.spartaclub.orderplatform.domain.review.presentation.dto.request.ReviewCreateRequestDto;
import com.spartaclub.orderplatform.domain.review.presentation.dto.request.ReviewSearchRequestDto;
import com.spartaclub.orderplatform.domain.review.presentation.dto.request.ReviewUpdateRequestDto;
import com.spartaclub.orderplatform.domain.review.presentation.dto.response.ReviewResponseDto;
import com.spartaclub.orderplatform.domain.review.presentation.dto.response.ReviewSearchResponseDto;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.global.auth.UserDetailsImpl;
import com.spartaclub.orderplatform.global.presentation.dto.ApiResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/*
 * Review 컨트롤러 클래스
 * 리뷰 관련 API Endpoint 제공
 *
 * @author 이준성
 * @date 2025-10-02(목)
 */
@Slf4j(topic = "Review Control")
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
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ReviewSearchResponseDto>>> searchReview(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @ModelAttribute ReviewSearchRequestDto dto) {
        User user = userDetails.getUser();
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(reviewService.searchReview(user, dto)));
    }

    // 리뷰 상세 조회 API
    @GetMapping("/search/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewSearchResponseDto>> searchDetailReview(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable UUID reviewId) {
        User user = userDetails.getUser();
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(reviewService.searchDetailReview(user, reviewId)));
    }

    // 리뷰 타입별 조회 API
    @GetMapping("/search/condition")
    public ResponseEntity<ApiResponse<Page<ReviewSearchResponseDto>>> searchConditionReview(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam("rating") Integer rating,
        @RequestParam("keyword") String keyword,
        Pageable pageable
    ) {
        User user = userDetails.getUser();
        log.info("Review condition search Controller rating : {} , keyword : {}, pageable: {}",
            rating, keyword, pageable);
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(
                reviewService.searchConditionReview(user, rating, keyword, pageable)));
    }

}
