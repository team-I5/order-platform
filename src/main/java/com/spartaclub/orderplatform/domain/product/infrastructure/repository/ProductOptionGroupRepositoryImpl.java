package com.spartaclub.orderplatform.domain.product.infrastructure.repository;

import com.spartaclub.orderplatform.domain.product.domain.entity.ProductOptionGroup;
import com.spartaclub.orderplatform.domain.product.domain.repository.ProductOptionGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ProductOptionGroupRepositoryImpl implements ProductOptionGroupRepository {

    private final ProductOptionGroupJPARepository productOptionGroupJPARepository;

    @Override
    public Optional<ProductOptionGroup> findById(UUID productOptionGroupId) {
        return productOptionGroupJPARepository.findById(productOptionGroupId);
    }

    @Override
    public ProductOptionGroup save(ProductOptionGroup group) {
        return productOptionGroupJPARepository.save(group);
    }
}
