package com.spartaclub.orderplatform.domain.product.infrastructure.repository;

import com.spartaclub.orderplatform.domain.product.domain.entity.ProductOptionItem;
import com.spartaclub.orderplatform.domain.product.domain.repository.ProductOptionItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ProductOptionItemRepositoryImpl implements ProductOptionItemRepository {

    private final ProductOptionItemJPARepository productOptionItemJPARepository;

    @Override
    public Optional<ProductOptionItem> findById(UUID productOptionItemId) {
        return productOptionItemJPARepository.findById(productOptionItemId);
    }
}
