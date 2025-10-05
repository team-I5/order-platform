//package com.spartaclub.orderplatform.domain.review.repository;
//
//import com.spartaclub.orderplatform.domain.review.entity.Review;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.Optional;
//import java.util.UUID;
//
/// *
// * Review Entity 레포지토리 인터페이스
// *
// * @author 이준성
// * @date 2025-10-01(수)
// */
//@Repository // spring 레포지토리 컴포넌트로 등록
//public interface ReviewRepository extends JpaRepository<Review, UUID> {
//    // 조회 메서드들 - Spring Data JPA가 메서드 이름으로 자동 쿼리 생성
//    Optional<Review> findByUserIdAndDeletedAtIsNull(UUID userId);         // 사용자 리뷰 조회
//
//    Optional<Review> findByStoreIdAndDeletedAtIsNull(UUID storeId);       // 음식점 리뷰 조회
//
//    Optional<Review> findByOrderIdAndDeletedAtIsNull(UUID orderId);       // 주문 리뷰 조회
//
//    Optional<Review> findByProductIdAndDeletedAtIsNull(UUID productId);   // 메뉴 리뷰 조회
//
//    Optional<Review> findByRatingAndDeletedAtIsNull(Integer rating);      // 별점으로 리뷰 조회
//
//    Optional<Review> findByContentsAndDeletedAtIsNull(String contents);   // 리뷰 내용으로 리뷰 조회
//
//    // 중복 검사 메서드 - Spring Data JPA가 메서드 이름으로 자동 쿼리 생성
//    boolean existsByOrderIdAndDeletedAtIsNull(UUID orderId);              // 주문 리뷰 중복 체크
//}
