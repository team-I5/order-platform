package com.spartaclub.orderplatform.domain.review.repository;

import com.spartaclub.orderplatform.domain.review.entity.Review;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/*
 * Review Entity 레포지토리 인터페이스
 *
 * @author 이준성
 * @date 2025-10-01(수)
 */
// SimpleJpaRepository에 @Repository 있어서 생략했음
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    // 조회 메서드들 - Spring Data JPA가 메서드 이름으로 자동 쿼리 생성
    Optional<Review> findByUser_UserIdAndDeletedAtIsNull(Long userId);         // 사용자 리뷰 조회

    Optional<Review> findByStore_StoreIdAndDeletedAtIsNull(UUID storeId);       // 음식점 리뷰 조회

    Optional<Review> findByOrder_OrderIdAndDeletedAtIsNull(UUID orderId);       // 주문 리뷰 조회

    Optional<Review> findByProduct_ProductIdAndDeletedAtIsNull(UUID productId);   // 메뉴 리뷰 조회

    Optional<Review> findByRatingAndDeletedAtIsNull(Integer rating);      // 별점으로 리뷰 조회

    Optional<Review> findByContentsAndDeletedAtIsNull(String contents);   // 리뷰 내용으로 리뷰 조회

    // 중복 검사 메서드 - Spring Data JPA가 메서드 이름으로 자동 쿼리 생성
    boolean existsByOrder_OrderIdAndDeletedAtIsNull(UUID orderId);              // 주문 리뷰 중복 체크

    @Query("""
           SELECT s.storeId, COALESCE(ROUND(AVG(r.rating), 1), 0), COUNT(r)
           FROM Store s
           LEFT JOIN Review r
                   ON r.store = s AND r.deletedAt IS NULL
           GROUP BY s.storeId
        """)
    List<Object[]> findReviewCountAndAverageForAllStores();


    @EntityGraph(attributePaths = {"user"})
    Page<Review> findAllByProduct_ProductId(UUID productId, Pageable pageable);


}
