package com.spartaclub.orderplatform.domain.product.domain.repository;

import com.spartaclub.orderplatform.domain.review.domain.model.Review;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductReviewReaderRepository {

    Page<Review> findAllByProduct_ProductId(UUID productId, Pageable pageable);
}
