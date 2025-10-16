package com.spartaclub.orderplatform.domain.product.infrastructure.repository;

import com.spartaclub.orderplatform.domain.product.domain.repository.ProductReviewReaderRepository;
import com.spartaclub.orderplatform.domain.review.domain.model.Review;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductReviewReaderRepositoryImpl implements ProductReviewReaderRepository {

    @Override
    public Page<Review> findAllByProduct_ProductId(UUID productId, Pageable pageable) {
        return null;
    }
}
