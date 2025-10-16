package com.spartaclub.orderplatform.domain.product.domain.repository;

import com.spartaclub.orderplatform.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProductReviewReaderRepository {
    Page<Review> findAllByProduct_ProductId(UUID productId, Pageable pageable);
}
