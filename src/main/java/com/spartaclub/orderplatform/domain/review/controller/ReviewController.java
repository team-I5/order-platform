package com.spartaclub.orderplatform.domain.review.controller;

import com.spartaclub.orderplatform.domain.review.service.ReviewService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * Review 컨트롤러 클래스
 * 리뷰 관련 API Endpoint 제공
 *
 * @author 이준성
 * @date 2025-10-02
 */
@RestController
@RequestMapping("/v1/reviews")
//@RequiredArgsConstructor
public class ReviewController {
    // 필드 선언
    private final ReviewService reviewService;

    // 생성자
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

}
