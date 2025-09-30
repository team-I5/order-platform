package com.spartaclub.orderplatform.domain.review.repository;

import com.spartaclub.orderplatform.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

    
}
