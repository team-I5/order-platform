package com.spartaclub.orderplatform.domain.store.infrastructure.repository;

import com.spartaclub.orderplatform.domain.store.domain.model.Store;
import com.spartaclub.orderplatform.domain.store.domain.model.StoreStatus;
import com.spartaclub.orderplatform.user.domain.entity.User;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, UUID> {

    boolean existsByUserAndStoreName(User owner, String storeName);

    Page<Store> findByStatus(StoreStatus storeStatus, Pageable pageable);

    Page<Store> findByUser(User user, Pageable pageable);

    Page<Store> findByStatusAndUser_UserId(StoreStatus status, Long userId, Pageable pageable);

    Page<Store> findByUser_UserId(Long userId, Pageable pageable);

    @Query("SELECT DISTINCT s FROM Store s " +
            "JOIN s.products p " +
            "WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "AND s.storeAddress LIKE CONCAT('%', :roadName, '%')")
    Page<Store> findStoresByProductNameAndRoadName(@Param("keyword") String keyword,
                                                   @Param("roadName") String roadName,
                                                   Pageable pageable);
}
