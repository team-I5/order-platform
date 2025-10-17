package com.spartaclub.orderplatform.domain.review.presentation.controller;

import com.spartaclub.orderplatform.domain.review.application.service.ReviewService;
import com.spartaclub.orderplatform.domain.review.presentation.dto.request.ReviewCreateRequestDto;
import com.spartaclub.orderplatform.domain.review.presentation.dto.request.ReviewUpdateRequestDto;
import com.spartaclub.orderplatform.domain.review.presentation.dto.response.ReviewResponseDto;
import com.spartaclub.orderplatform.domain.review.presentation.dto.response.ReviewSearchResponseDto;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.global.auth.UserDetailsImpl;
import com.spartaclub.orderplatform.global.presentation.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
@Tag(name = "Review", description = "리뷰 관리 API")
@RestController // @ResponseBody + @Controller
@RequiredArgsConstructor // final 제어자가 붙은 필드에 대한 생성자 생성
@RequestMapping("/v1/reviews")
public class ReviewController {

    // 필드 선언
    private final ReviewService reviewService;

    /**
     * 리뷰 등록 API - 인증된 사용자 주문 건에 새로운 리뷰 등록
     *
     * @param userDetails 인증된 사용자 정보
     * @param requestDto  리뷰 등록 요청 데이터
     */
    @Operation(summary = "리뷰 등록", description = "고객 주문에 하나의 리뷰만 등록 가능합니다.")
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponseDto>> createReview(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @Valid @RequestBody ReviewCreateRequestDto requestDto) {
        // 사용자 객체 받아옴
        User user = userDetails.getUser();
        // 리뷰 등록
        ReviewResponseDto responseDto = reviewService.createReview(user, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(responseDto));
    }

    /**
     * 리뷰 수정 API - 인증된 사용자 주문 리뷰 수정
     *
     * @param userDetails 인증된 사용자 정보
     * @param reviewId    수정할 리뷰 ID
     * @param requestDto  리뷰 수정 요청 데이터
     * @return 수정된 리뷰 정보
     */
    @Operation(summary = "리뷰 수정", description = "인증된 사용자의 주문 리뷰를 수정합니다.")
    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponseDto>> updateReview(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable UUID reviewId,
        @Valid @RequestBody ReviewUpdateRequestDto requestDto) {
        // 사용자 객체 받아옴
        User user = userDetails.getUser();
        // 리뷰 수정
        ReviewResponseDto responseDto = reviewService.updateReview(user, reviewId, requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto));
    }

    /**
     * 리뷰 삭제 API (soft delete) - 인증된 사용자 주문 리뷰 삭제
     *
     * @param userDetails 인증된 사용자 정보
     * @param reviewId    삭제할 리뷰 ID
     * @return 응답 없음
     */
    @Operation(summary = "리뷰 삭제", description = "인증된 사용자의 주문 리뷰 삭제합니다.")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable UUID reviewId) {
        // 사용자 객체 받아옴
        User user = userDetails.getUser();
        // 리뷰 삭제
        reviewService.deleteReview(user, reviewId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success());
    }

    /**
     * 리뷰 목록 조회 API - 인증된 사용자 주문 리뷰 목록 조회
     *
     * @param userDetails 인증된 사용자 정보
     * @param storeName   가게 이름
     * @param pageable    페이징 처리
     * @return 리뷰 목록 정보
     */
    @Operation(summary = "리뷰 조회", description = "인증된 사용자 주문 리뷰 목록 조회합니다.")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ReviewSearchResponseDto>>> searchReview(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam String storeName,
        Pageable pageable) {
        // 사용자 객체 받아옴
        User user = userDetails.getUser();
        // 리뷰 목록 조회
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(
                reviewService.searchReview(user, storeName, pageable)));
    }

    /**
     * 리뷰 상세 조회 API - 주문한 사용자 리뷰 조회
     *
     * @param reviewId 조회할 리뷰 ID
     * @return 특정 리뷰 정보
     */
    @Operation(summary = "리뷰 상세 조회", description = "인증된 사용자 주문 리뷰 조회합니다.")
    @GetMapping("/search/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewSearchResponseDto>> searchDetailReview(
        @PathVariable UUID reviewId) {
        // 리뷰 상세 조회
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(reviewService.searchDetailReview(reviewId)));
    }

    /**
     * 리뷰 조건별 조회 API - 파리미터 조건별 리뷰 조회
     *
     * @param userDetails 인증된 사용자 정보
     * @param rating      별점
     * @param pageable    페이징 처리
     * @return 조건별 리뷰 목록 정보
     */
    @Operation(summary = "리뷰 조건별 조회1", description = "인증된 사용자 리뷰 별점 별로 조회합니다.")
    @GetMapping("/search/condition1")
    public ResponseEntity<ApiResponse<Page<ReviewSearchResponseDto>>> searchConditionReviewOne(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam("rating") Integer rating,
        Pageable pageable
    ) {
        // 사용자 객체 받아옴
        User user = userDetails.getUser();
        log.info("Review condition search Controller rating : {} , pageable: {}",
            rating, pageable);
        // 리뷰 조건별 조회
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(
                reviewService.searchConditionReviewOne(user, rating, pageable)));
    }

    /**
     * 리뷰 조건별 조회 API - 파리미터 조건별 리뷰 조회
     *
     * @param userDetails 인증된 사용자 정보
     * @param keyword     리뷰 내용 키워드
     * @param pageable    페이징 처리
     * @return 조건별 리뷰 목록 정보
     */
    @Operation(summary = "리뷰 조건별 조회2", description = "인증된 사용자 리뷰 키워드로 조회합니다.")
    @GetMapping("/search/condition2")
    public ResponseEntity<ApiResponse<Page<ReviewSearchResponseDto>>> searchConditionReviewTwo(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam("keyword") String keyword,
        Pageable pageable
    ) {
        // 사용자 객체 받아옴
        User user = userDetails.getUser();
        log.info("Review condition search Controller keyword : {}, pageable: {}",
            keyword, pageable);
        // 리뷰 조건별 조회
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(
                reviewService.searchConditionReviewTwo(user, keyword, pageable)));
    }


}