package com.spartaclub.orderplatform.domain.product.infrastructure.repository;

import com.spartaclub.orderplatform.domain.product.domain.repository.ProductStoreReaderRepository;
import com.spartaclub.orderplatform.domain.store.domain.model.Store;
import com.spartaclub.orderplatform.domain.store.infrastructure.repository.StoreJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ProductStoreReaderRepositoryImpl implements ProductStoreReaderRepository {

    private final StoreJpaRepository storeJpaRepository;

    @Override
    public Page<Store> findDistinctByProductNameContainingIgnoreCase(String keyword, String roadName, Pageable pageable) {
        return storeJpaRepository.findDistinctByProductNameContainingIgnoreCase(keyword, roadName, pageable);
    }

    @Override
    public Optional<Store> findById(UUID storeId) {
        return storeJpaRepository.findById(storeId);
    }
}
