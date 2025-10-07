package com.spartaclub.orderplatform.domain.review.service;


import com.spartaclub.orderplatform.domain.review.dto.ReviewRequestDto;
import com.spartaclub.orderplatform.domain.review.entity.Review;
import com.spartaclub.orderplatform.domain.review.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

/*
 * Review 서비스 클래스
 * 리뷰 관련 비즈니스 로직 처리
 *
 * @author 이준성
 * @date 2025-10-02
 */
@Service
//@RequiredArgsConstructor
public class ReviewService {
    // 필드 선언
    private final ReviewRepository reviewRepository;

    // 생성자
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    // create
//    public ReviewResponseDto writeReview(ReviewRequestDto requestDto) {
//
//
//    }
    // read


    // find UUID
    public Review findReview(UUID id) {
        return reviewRepository.findById(id).orElseThrow(NullPointerException::new);
    }

    /*
     * 중복 데이터 검증
     * 주문 리뷰 중복 여부 확인
     *
     * @param requestDto 리뷰 요청 데이터
     * @throws RuntimeException 발생 - 중복 데이터 발견될 때
     */
    private void validateDuplicateData(ReviewRequestDto requestDto) {
        if (reviewRepository.existsByOrderIdAndDeletedAtIsNull(requestDto.getOrderId())) {
            throw new RuntimeException("리뷰가 존재하는 주문입니다.");
        }
    }
}
