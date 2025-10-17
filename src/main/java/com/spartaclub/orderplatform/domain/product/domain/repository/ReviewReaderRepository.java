package com.spartaclub.orderplatform.domain.product.domain.repository;

import com.spartaclub.orderplatform.domain.review.domain.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * 상품의 리뷰 조회를 위한 Repository 인터페이스
 * Product 도메인에서 Review 정보를 읽기 전용으로 조회할 때 사용
 */
public interface ReviewReaderRepository {
    
    /**
     * 특정 상품의 리뷰 목록을 페이징하여 조회
     * 
     * @param productId 상품 ID
     * @param pageable 페이징 정보
     * @return 리뷰 페이지 정보
     */
    Page<Review> findAllByProduct_ProductId(UUID productId, Pageable pageable);
}