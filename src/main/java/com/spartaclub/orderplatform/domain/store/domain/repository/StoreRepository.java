package com.spartaclub.orderplatform.domain.store.domain.repository;

import com.spartaclub.orderplatform.domain.category.domain.model.CategoryType;
import com.spartaclub.orderplatform.domain.store.domain.model.Store;
import com.spartaclub.orderplatform.domain.store.domain.model.StoreStatus;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository {

    boolean existsByUserAndStoreName(User owner, String storeName);

    Page<Store> findByStatusAndDeletedAtIsNull(StoreStatus storeStatus, Pageable pageable);

    Page<Store> findByUser(User user, Pageable pageable);

    Page<Store> findByStatusAndUser_UserId(StoreStatus storeStatus, Long userId, Pageable pageable);

    Page<Store> findByStatus(StoreStatus status, Pageable pageable);

    Page<Store> findByUser_UserId(Long userId, Pageable pageable);

    Page<Store> findApprovedStoreByCategory(CategoryType type, Pageable pageable);

    Page<Store> findOwnerApprovedStoreByCategory(CategoryType type, Long userId, Pageable pageable);

    Page<Store> findAllStoreByCategory(CategoryType type, Pageable pageable);

    Optional<Store> findById(UUID storeId);

    Page<Store> findAll(Pageable pageable);

    Store save(Store store);

    Map<UUID, Store> findAllById(Collection<UUID> storeIds);

    Page<Store> findApprovedStoresByStoreName(
        String storeName, StoreStatus status, Pageable pageable);

    Page<Store> findDistinctByProductNameContainingIgnoreCase(String keyword,
                                                              String roadName,
                                                              Pageable pageable);
}