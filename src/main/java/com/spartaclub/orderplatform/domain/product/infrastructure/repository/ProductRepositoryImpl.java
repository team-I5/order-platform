package com.spartaclub.orderplatform.domain.product.infrastructure.repository;


import com.spartaclub.orderplatform.domain.product.domain.entity.Product;
import com.spartaclub.orderplatform.domain.product.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJPARepository productJPARepository;

    @Override
    public Product save(Product product) {
        return productJPARepository.save(product);
    }

    @Override
    public Page<Product> findByStore_StoreIdAndIsHiddenFalseAndDeletedAtIsNull(UUID storeId, Pageable pageable) {
        return productJPARepository.findByStore_StoreIdAndIsHiddenFalseAndDeletedAtIsNull(storeId, pageable);
    }

    @Override
    public Optional<Product> findWithOptionGroupsAndItemsByProductId(UUID productId) {
        return productJPARepository.findWithOptionGroupsAndItemsByProductId(productId);
    }

    @Override
    public Optional<Product> findById(UUID productId) {
        return productJPARepository.findById(productId);
    }
}
