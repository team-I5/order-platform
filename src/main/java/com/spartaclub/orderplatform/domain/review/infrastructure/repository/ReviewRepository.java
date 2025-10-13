package com.spartaclub.orderplatform.domain.review.infrastructure.repository;

import com.spartaclub.orderplatform.domain.review.domain.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

/*
 * Review Entity 레포지토리 인터페이스
 *
 * @author 이준성
 * @date 2025-10-01(수)
 */
// SimpleJpaRepository에 @Repository 있어서 생략했음
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    // 조회 메서드들 - Spring Data JPA가 메서드 이름으로 자동 쿼리 생성
    Page<Review> findByUser_UserIdAndDeletedAtIsNull(Long userId, Pageable pageable);         // 사용자 리뷰 조회

    Page<Review> findByStore_StoreIdAndDeletedAtIsNull(UUID storeId, Pageable pageable);       // 음식점 리뷰 조회

    Page<Review> findByOrder_OrderIdAndDeletedAtIsNull(UUID orderId, Pageable pageable);       // 주문 리뷰 조회

    Page<Review> findByProduct_ProductIdAndDeletedAtIsNull(UUID productId, Pageable pageable);   // 메뉴 리뷰 조회

    Page<Review> findByRatingAndDeletedAtIsNull(Integer rating, Pageable pageable);      // 별점으로 리뷰 조회

    Page<Review> findByContentsAndDeletedAtIsNull(String contents, Pageable pageable);   // 리뷰 내용으로 리뷰 조회

    // 중복 검사 메서드 - Spring Data JPA가 메서드 이름으로 자동 쿼리 생성
    boolean existsByOrder_OrderIdAndDeletedAtIsNull(UUID orderId);              // 주문 리뷰 중복 체크

    // 평점과 리뷰 개수 가져와는 쿼리 추가
    @Query("""
               SELECT s.storeId, COALESCE(ROUND(AVG(r.rating), 1), 0), COUNT(r)
               FROM Store s
               LEFT JOIN Review r
                       ON r.store = s AND r.deletedAt IS NULL
               GROUP BY s.storeId
            """)
    List<Object[]> findReviewCountAndAverageForAllStores();
}
