package com.spartaclub.orderplatform.domain.product.infrastructure.repository;

import com.spartaclub.orderplatform.domain.product.domain.repository.ReviewReaderRepository;
import com.spartaclub.orderplatform.domain.review.domain.model.Review;
import com.spartaclub.orderplatform.domain.review.infrastructure.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ReviewReaderRepositoryImpl implements ReviewReaderRepository {

    private final ReviewRepository reviewRepository;

    @Override
    public Page<Review> findAllByProduct_ProductId(UUID productId, Pageable pageable) {
        return reviewRepository.findAllByProduct_ProductId(productId, pageable);
    }
}