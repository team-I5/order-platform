package com.spartaclub.orderplatform.domain.product.domain.repository;

import com.spartaclub.orderplatform.domain.product.domain.entity.ProductOptionGroup;

import java.util.Optional;
import java.util.UUID;

public interface ProductOptionGroupRepository {
    Optional<ProductOptionGroup> findById(UUID productOptionGroupId);

    ProductOptionGroup save(ProductOptionGroup group);
}
