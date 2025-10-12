package com.spartaclub.orderplatform.domain.review.application.service;


import com.spartaclub.orderplatform.domain.review.application.mapper.ReviewMapper;
import com.spartaclub.orderplatform.domain.review.domain.model.Review;
import com.spartaclub.orderplatform.domain.review.infrastructure.repository.ReviewRepository;
import com.spartaclub.orderplatform.domain.review.presentation.dto.request.ReviewCreateRequestDto;
import com.spartaclub.orderplatform.domain.review.presentation.dto.request.ReviewSearchRequestDto;
import com.spartaclub.orderplatform.domain.review.presentation.dto.request.ReviewUpdateRequestDto;
import com.spartaclub.orderplatform.domain.review.presentation.dto.response.ReviewResponseDto;
import com.spartaclub.orderplatform.domain.review.presentation.dto.response.ReviewSearchResponseDto;
import com.spartaclub.orderplatform.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
@Transactional(readOnly = true)
public class ReviewService {
    // 필드 선언
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    // 리뷰 등록 로직(create)
    public ReviewResponseDto createReview(User user, ReviewCreateRequestDto reviewCreateRequestDto) {
        // 1. 주문별 중복 검증
        boolean existReview = reviewRepository.existsByOrder_OrderIdAndDeletedAtIsNull(reviewCreateRequestDto.getOrderId());
        if (existReview) {
            throw new RuntimeException("리뷰가 존재하는 주문입니다.");
        }
        // 2. requestDto → entity 전환
        Review review = reviewMapper.toReviewEntity(reviewCreateRequestDto);
        // 3. DB 저장 후 entity → responseDto 전환
        return reviewMapper.toReviewDto(reviewRepository.save(review));
    }

    // 리뷰 수정 로직(update)
    @Transactional
    public ReviewResponseDto updateReview(User user, UUID reviewId, ReviewUpdateRequestDto reviewUpdateRequestDto) {
        // 1. reviewId로 해당 리뷰 DB 존재 확인
        Review review = findReview(reviewId);
        // 2. 리뷰 수정자 일치 확인
        if (!review.getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("본인 작성 리뷰만 수정할 수 있습니다.");
        }
        // 3. JPA 변경 감지(dirty checking)으로 자동 저장
        // @Transactional안에서 entity transaction에 의해 et.commit()이 호출될 때 DB반영
        // 4. entity → responseDto 변환 뒤 반환
        ReviewResponseDto responseDto = reviewMapper.toReviewDto(review);
        // 5. 반환 한 곳에서 리뷰 정보 수정
        responseDto.updateReview(reviewUpdateRequestDto);
        return responseDto;
    }

    // 리뷰 삭제 로직(delete)
    @Transactional
    public void deleteReview(UUID reviewId) {
        // 1. reviewId로 해당 리뷰 DB 존재 확인
        Review review = findReview(reviewId);
        // 2. 리뷰 도메인 삭제 메서드 호출
        review.deleteReview(0L);
    }

    // 리뷰 조건별 조회
    // 지연 로딩이라서 트랜잭션 붙였는데, 성능 향상 위해 readonly 옵션 true로 설정
    @Transactional(readOnly = true)
    public Page<ReviewSearchResponseDto> searchReview(ReviewSearchRequestDto dto) {
        Pageable pageable = PageRequest.of(
                dto.getPage(), dto.getSize(),
                Sort.by(dto.getDirection(), "createdAt")
        );
        if (dto.getUserId() != null) {
            return reviewRepository.findByUser_UserIdAndDeletedAtIsNull(dto.getUserId(), pageable)
                    .map(reviewMapper::toReviewSearchResponseDto);
        } else if (dto.getStoreId() != null) {
            return reviewRepository.findByStore_StoreIdAndDeletedAtIsNull(dto.getStoreId(), pageable)
                    .map(reviewMapper::toReviewSearchResponseDto);
        } else if (dto.getOrderId() != null) {
            return reviewRepository.findByOrder_OrderIdAndDeletedAtIsNull(dto.getOrderId(), pageable)
                    .map(reviewMapper::toReviewSearchResponseDto);
        } else if (dto.getProductId() != null) {
            return reviewRepository.findByProduct_ProductIdAndDeletedAtIsNull(dto.getProductId(), pageable)
                    .map(reviewMapper::toReviewSearchResponseDto);
        } else if (dto.getRating() != null) {
            return reviewRepository.findByRatingAndDeletedAtIsNull(dto.getRating(), pageable)
                    .map(reviewMapper::toReviewSearchResponseDto);
        } else if (dto.getContents() != null) {
            return reviewRepository.findByContentsAndDeletedAtIsNull(dto.getContents(), pageable)
                    .map(reviewMapper::toReviewSearchResponseDto);
        } else {
            return reviewRepository.findAll(pageable).map(reviewMapper::toReviewSearchResponseDto);
        }
    }

    // 존재하는 리뷰인지 확인
    public Review findReview(UUID id) {
        return reviewRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 리뷰입니다."));
    }
}
