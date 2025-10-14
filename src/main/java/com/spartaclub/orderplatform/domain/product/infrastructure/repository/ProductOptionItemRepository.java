package com.spartaclub.orderplatform.domain.product.infrastructure.repository;

import com.spartaclub.orderplatform.domain.product.domain.entity.ProductOptionItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductOptionItemRepository extends JpaRepository<ProductOptionItem, UUID> {
}
