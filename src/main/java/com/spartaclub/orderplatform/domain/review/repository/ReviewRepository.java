package com.spartaclub.orderplatform.domain.review.repository;

import com.spartaclub.orderplatform.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/*
 * Review Entity 레포지토리 인터페이스
 *
 * @author 이준성
 * @date 2025-10-01(수)
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    Optional<Review> findByRating(Integer rating);      // 별점으로 리뷰 조회

    Optional<Review> findByContents(String contents);   // 리뷰 내용으로 리뷰 조회


}
