package com.spartaclub.orderplatform.domain.product.domain.repository;

import com.spartaclub.orderplatform.domain.product.domain.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
    Product save(Product product);

    Page<Product> findByStore_StoreIdAndIsHiddenFalseAndDeletedAtIsNull(UUID storeId, Pageable pageable);

    Optional<Product> findWithOptionGroupsAndItemsByProductId(UUID productId);

    Optional<Product> findById(UUID productId);
}
