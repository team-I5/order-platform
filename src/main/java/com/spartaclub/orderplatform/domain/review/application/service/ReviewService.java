package com.spartaclub.orderplatform.domain.review.application.service;


import com.spartaclub.orderplatform.domain.order.application.OrderService;
import com.spartaclub.orderplatform.domain.order.domain.model.Order;
import com.spartaclub.orderplatform.domain.product.application.service.ProductService;
import com.spartaclub.orderplatform.domain.product.domain.entity.Product;
import com.spartaclub.orderplatform.domain.review.application.mapper.ReviewMapper;
import com.spartaclub.orderplatform.domain.review.domain.model.Review;
import com.spartaclub.orderplatform.domain.review.infrastructure.repository.ReviewRepository;
import com.spartaclub.orderplatform.domain.review.presentation.dto.request.ReviewCreateRequestDto;
import com.spartaclub.orderplatform.domain.review.presentation.dto.request.ReviewUpdateRequestDto;
import com.spartaclub.orderplatform.domain.review.presentation.dto.response.ReviewResponseDto;
import com.spartaclub.orderplatform.domain.review.presentation.dto.response.ReviewSearchResponseDto;
import com.spartaclub.orderplatform.domain.store.application.service.StoreService;
import com.spartaclub.orderplatform.domain.store.domain.model.Store;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/*
 * Review 서비스 클래스
 * 리뷰 관련 비즈니스 로직 처리
 *
 * @author 이준성
 * @date 2025-10-02
 */
@Slf4j(topic = "Review Service")
@Service
@RequiredArgsConstructor // final 제어자가 붙은 필드에 대한 생성자 생성
public class ReviewService {

    // 필드 선언
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final OrderService orderService;
    private final StoreService storeService;
    private final ProductService productService;

    // 리뷰 등록 로직(create)
    @Transactional
    public ReviewResponseDto createReview(User user,
        ReviewCreateRequestDto requestDto) {
        // 1. 주문별 중복 검증
        boolean existReview = reviewRepository.existsByOrder_OrderIdAndDeletedAtIsNull(
            requestDto.getOrderId());
        if (existReview) {
            throw new IllegalArgumentException("리뷰가 존재하는 주문입니다.");
        }
        // 주문 조회
        Order order = orderService.findById(requestDto.getOrderId());
        // 가게 조회
        Store store = storeService.getStore(requestDto.getStoreId());
        // 상품 조회
        Product product = productService.findProductOrThrow(requestDto.getProductId());
        // 2. 리뷰 객체 생성 By 정적 팩토리 메서드
        Review review = Review.create(user, store, product, order, requestDto.getRating(),
            requestDto.getContents());
        // 3. DB 저장 후 entity → responseDto 전환
        return reviewMapper.toReviewDto(reviewRepository.save(review));
    }

    // 리뷰 수정 로직(update)
    @Transactional
    public ReviewResponseDto updateReview(User user, UUID reviewId, ReviewUpdateRequestDto dto) {
        // 1. reviewId로 해당 리뷰 DB 존재 확인
        Review review = findReview(reviewId);
        // 2. 리뷰 수정자 일치 확인
        if (!review.getCreatedId().equals(user.getUserId())) {
            throw new IllegalArgumentException("본인 작성 리뷰만 수정할 수 있습니다.");
        }
        // 3. 리뷰 엔티티 update 함수에서 변경된 값 반영
        review.updateReview(dto.getRating(), dto.getContents());
        // 4. dirty checking후 et.commit()이 호출될 때 DB 반영
        // @Transactional안에서 JPA 변경 감지(dirty checking) 되면 entity transaction에 의해 et.commit()이 호출될 때 DB반영
        // 5. entity → responseDto 변환 뒤 반환
        return reviewMapper.toReviewDto(review);
    }

    // 리뷰 삭제 로직(delete)
    @Transactional
    public void deleteReview(User user, UUID reviewId) {
        // 1. reviewId로 해당 리뷰 DB 존재 확인
        Review review = findReview(reviewId);
        // 2. 리뷰 삭제자 일치 확인
        if (!review.getCreatedId().equals(user.getUserId())) {
            throw new IllegalArgumentException("본인 작성 리뷰만 삭제할 수 있습니다.");
        }
        // 3. 리뷰 도메인 삭제 메서드 호출
        review.deleteReview(user.getUserId());
    }

    // 리뷰 목록 조회
    // 지연 로딩이라서 트랜잭션 붙였는데, 성능 향상 위해 readonly 옵션 true로 설정
    @Transactional(readOnly = true)
    public Page<ReviewSearchResponseDto> searchReview(User user, UUID orderId, String storeName,
        Pageable pageable) {
        return switch (user.getRole()) {
            case CUSTOMER -> searchReviewForCustomer(user, orderId, pageable);
            case OWNER -> searchReviewForOwner(storeName, pageable);
            case MANAGER, MASTER -> searchReviewForAdmin(pageable);
        };
    }

    // 고객 리뷰 조회
    private Page<ReviewSearchResponseDto> searchReviewForCustomer(User user,
        UUID orderId, Pageable pageable) {
        return reviewRepository.findByUser_UserIdAndOrder_OrderIdAndDeletedAtIsNull(
                user.getUserId(), orderId, pageable)
            .map(reviewMapper::toReviewSearchResponseDto);
    }

    // 음식점 주인 리뷰 조회
    private Page<ReviewSearchResponseDto> searchReviewForOwner(String storeName,
        Pageable pageable) {
        return reviewRepository.findByStore_StoreNameAndDeletedAtIsNull(storeName, pageable)
            .map(reviewMapper::toReviewSearchResponseDto);
    }

    // MANAGER, MASTER 리뷰 조회
    private Page<ReviewSearchResponseDto> searchReviewForAdmin(Pageable pageable) {
        return reviewRepository.findAllBy(pageable)
            .map(reviewMapper::toReviewSearchResponseDto);
    }
    
    // 리뷰 상세 조회
    // 지연 로딩이라서 트랜잭션 붙였는데, 성능 향상 위해 readonly 옵션 true로 설정
    @Transactional(readOnly = true)
    public ReviewSearchResponseDto searchDetailReview(UUID reviewId) {
        Review review = findReview(reviewId);
        return reviewMapper.toReviewSearchResponseDto(review);
    }

    // 리뷰 조건별 조회
    @Transactional(readOnly = true)
    public Page<ReviewSearchResponseDto> searchConditionReview(
        User user, Integer rating,
        String keyword,
        Pageable pageable) {
        if (rating != null) {
            return reviewRepository.findByRatingAndDeletedAtIsNull(rating, pageable)
                .map(reviewMapper::toReviewSearchResponseDto);
        } else if (keyword != null) {
            return reviewRepository.findByContentsContainingAndDeletedAtIsNull(keyword,
                    pageable)
                .map(reviewMapper::toReviewSearchResponseDto);
        }
        return Page.empty();
    }

    // 존재하는 리뷰인지 확인
    @Transactional
    public Review findReview(UUID id) {
        return reviewRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 리뷰입니다."));
    }


}
