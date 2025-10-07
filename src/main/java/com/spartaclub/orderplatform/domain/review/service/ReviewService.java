package com.spartaclub.orderplatform.domain.review.service;


import com.spartaclub.orderplatform.domain.review.dto.ReviewCreateRequestDto;
import com.spartaclub.orderplatform.domain.review.dto.ReviewResponseDto;
import com.spartaclub.orderplatform.domain.review.dto.ReviewUpdateRequestDto;
import com.spartaclub.orderplatform.domain.review.entity.Review;
import com.spartaclub.orderplatform.domain.review.mapper.ReviewMapper;
import com.spartaclub.orderplatform.domain.review.repository.ReviewRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

/*
 * Review 서비스 클래스
 * 리뷰 관련 비즈니스 로직 처리
 *
 * @author 이준성
 * @date 2025-10-02
 */
@Service
@RequiredArgsConstructor
public class ReviewService {
    // 필드 선언
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    // 리뷰 등록 로직(create)
    public ReviewResponseDto createReview(@Valid ReviewCreateRequestDto reviewCreateRequestDto) {
        // 1. requestDto → entity 전환
        Review review = reviewMapper.toReviewEntity(reviewCreateRequestDto);
        // 2. 저장
        Review saveReview = reviewRepository.save(review);
        // 3. entity → responseDto 전환
        return reviewMapper.toReviewDto(saveReview);
    }

    // 리뷰 수정 로직(update)
    @Transactional
    public ReviewResponseDto updateReview(UUID reviewId, @Valid ReviewUpdateRequestDto reviewUpdateRequestDto) {
        // 1. reviewId로 해당 리뷰 DB 존재 확인
        Review review = findReview(reviewId);
        // 2. 리뷰 정보 수정
        review.updateReview(reviewUpdateRequestDto);
        // 3. entity → dto 변환 뒤 반환
        return reviewMapper.toReviewDto(review);
    }

    // 리뷰 삭제 로직(delete)
    @Transactional
    public void deleteReview(UUID reviewId) {
        // 1. reviewId로 해당 리뷰 DB 존재 확인
        Review review = findReview(reviewId);
        review.deleteReview(0L);
    }

    // 리뷰 목록 조회 로직(read)


    // find UUID
    public Review findReview(UUID id) {
        return reviewRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "리뷰가 존재하지 않습니다."));
    }

    /*
     * 중복 데이터 검증
     * 주문 리뷰 중복 여부 확인
     *
     * @param requestDto 리뷰 요청 데이터
     * @throws RuntimeException 발생 - 중복 데이터 발견될 때
     */
    private void validateDuplicateData(ReviewCreateRequestDto requestDto) {
        if (reviewRepository.existsByOrderIdAndDeletedAtIsNull(requestDto.getOrderId())) {
            throw new RuntimeException("리뷰가 존재하는 주문입니다.");
        }
    }
}
