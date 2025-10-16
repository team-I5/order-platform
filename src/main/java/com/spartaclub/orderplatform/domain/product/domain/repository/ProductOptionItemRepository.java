package com.spartaclub.orderplatform.domain.product.domain.repository;

import com.spartaclub.orderplatform.domain.product.domain.entity.ProductOptionItem;

import java.util.Optional;
import java.util.UUID;

public interface ProductOptionItemRepository {
    Optional<ProductOptionItem> findById(UUID productOptionItemId);
}
