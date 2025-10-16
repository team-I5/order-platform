package com.spartaclub.orderplatform.domain.product.domain.repository;

import com.spartaclub.orderplatform.domain.store.domain.model.Store;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ProductStoreReaderRepository {
    Page<Store> findDistinctByProductNameContainingIgnoreCase(String keyword, String roadName, Pageable pageable);

    Optional<Store> findById(@NotNull(message = "스토어 ID는 필수입니다.") UUID storeId);
}
