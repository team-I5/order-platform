package com.spartaclub.orderplatform.domain.store.infrastructure.repository;

import com.spartaclub.orderplatform.domain.store.domain.model.Store;
import com.spartaclub.orderplatform.domain.store.domain.model.StoreStatus;
import com.spartaclub.orderplatform.domain.store.domain.repository.StoreRepository;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StoreRepositoryImpl implements StoreRepository {

    private final StoreJpaRepository storeJpaRepository;

    @Override
    public boolean existsByUserAndStoreName(User user, String storeName) {
        return storeJpaRepository.existsByUserAndStoreName(user, storeName);
    }

    @Override
    public Page<Store> findByStatusAndDeletedAtIsNull(StoreStatus storeStatus, Pageable pageable) {
        return storeJpaRepository.findByStatusAndDeletedAtIsNull(storeStatus, pageable);
    }

    @Override
    public Page<Store> findByUser(User user, Pageable pageable) {
        return storeJpaRepository.findByUser(user, pageable);
    }

    @Override
    public Page<Store> findByStatusAndUser_UserId(StoreStatus storeStatus, Long userId,
        Pageable pageable) {
        return storeJpaRepository.findByStatusAndUser_UserId(storeStatus, userId, pageable);
    }

    @Override
    public Page<Store> findByStatus(StoreStatus status, Pageable pageable) {
        return storeJpaRepository.findByStatus(status, pageable);
    }

    @Override
    public Page<Store> findByUser_UserId(Long userId, Pageable pageable) {
        return storeJpaRepository.findByUser_UserId(userId, pageable);
    }

    @Override
    public Page<Store> findApprovedStoreByCategory(String categoryType, Pageable pageable) {
        return storeJpaRepository.findApprovedStoreByCategory(categoryType, pageable);
    }

    @Override
    public Page<Store> findOwnerApprovedStoreByCategory(
        String categoryType, Long userId, Pageable pageable
    ) {
        return storeJpaRepository.findOwnerApprovedStoreByCategory(categoryType, userId, pageable);
    }

    @Override
    public Page<Store> findAllStoreByCategory(String categoryType, Pageable pageable) {
        return storeJpaRepository.findAllStoreByCategory(categoryType, pageable);
    }

    @Override
    public Optional<Store> findById(UUID storeId) {
        return storeJpaRepository.findById(storeId);
    }

    @Override
    public Page<Store> findAll(Pageable pageable) {
        return storeJpaRepository.findAll(pageable);
    }

    @Override
    public Store save(Store store) {
        return storeJpaRepository.save(store);
    }

    @Override
    public Map<UUID, Store> findAllById(Collection<UUID> storeIds) {
        return storeJpaRepository.findAllById(storeIds).stream()
            .collect(Collectors.toMap(Store::getStoreId, Function.identity()));
    }

    @Override
    public Page<Store> findApprovedStoresByStoreName(
        String storeName, StoreStatus status, Pageable pageable
    ) {
        return storeJpaRepository.findApprovedStoresByStoreName(storeName, status, pageable);
    }

    @Override
    public Page<Store> findDistinctByProductNameContainingIgnoreCase(
        String keyword, String roadName, Pageable pageable
    ) {
        return storeJpaRepository
            .findDistinctByProductNameContainingIgnoreCase(keyword, roadName, pageable);
    }
}
