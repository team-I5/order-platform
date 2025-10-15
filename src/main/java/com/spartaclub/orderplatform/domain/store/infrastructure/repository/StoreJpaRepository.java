package com.spartaclub.orderplatform.domain.store.infrastructure.repository;

import com.spartaclub.orderplatform.domain.category.entity.CategoryType;
import com.spartaclub.orderplatform.domain.store.domain.model.Store;
import com.spartaclub.orderplatform.domain.store.domain.model.StoreStatus;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StoreJpaRepository extends JpaRepository<Store, UUID> {

    boolean existsByUserAndStoreName(User owner, String storeName);

    Page<Store> findByStatusAndDeletedAtIsNull(StoreStatus storeStatus, Pageable pageable);

    Page<Store> findByUser(User user, Pageable pageable);

    Page<Store> findByStatusAndUser_UserId(StoreStatus storeStatus, Long userId, Pageable pageable);

    Page<Store> findByStatus(StoreStatus status, Pageable pageable);

    Page<Store> findByUser_UserId(Long userId, Pageable pageable);

    @Query("""
         SELECT DISTINCT s
         FROM Store s
                 JOIN s.storeCategories sc
                 JOIN sc.category c
         WHERE c.type = :type
                 AND s.status = 'APPROVED'
                 AND s.deletedAt IS NULL
                 AND sc.deletedAt IS NULL
                 AND c.deletedAt IS NULL
        """)
    Page<Store> findApprovedStoreByCategory(@Param("type") CategoryType type, Pageable pageable);

    @Query("""
         SELECT DISTINCT s
         FROM Store s
                 JOIN s.storeCategories sc
                 JOIN sc.category c
         WHERE c.type = :type
                 AND s.status = 'APPROVED'
                 AND s.user.userId = :userId
                 AND s.deletedAt IS NULL
                 AND sc.deletedAt IS NULL
                 AND c.deletedAt IS NULL
        """)
    Page<Store> findOwnerApprovedStoreByCategory(@Param("type") CategoryType type,
        @Param("userId") Long userId, Pageable pageable);

    @Query("""
         SELECT DISTINCT s
         FROM Store s
                 JOIN s.storeCategories sc
                 JOIN sc.category c
         WHERE c.type = :type
                 AND s.status = 'APPROVED'
                 AND sc.deletedAt IS NULL
                 AND c.deletedAt IS NULL
        """)
    Page<Store> findAllStoreByCategory(@Param("type") CategoryType type, Pageable pageable);

//    @Query("""
//            SELECT s
//            FROM Store s
//            WHERE s.deletedAt IS NULL
//                      AND s.status = :status
//                      AND LOWER(s.storeName) LIKE LOWER(CONCAT('%', :storeName, '%'))
//        """)
//    Page<Store> findApprovedStoresByStoreName(@Param("storeName") String storeName,
//        @Param("status") StoreStatus status, Pageable pageable);

}
