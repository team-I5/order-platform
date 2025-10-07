package com.spartaclub.orderplatform.domain.store.repository;

import com.spartaclub.orderplatform.domain.store.entity.Store;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StoreRepository extends JpaRepository<Store, UUID> {
    @Query("SELECT DISTINCT s FROM Store s " +
            "JOIN s.products p " +
            "WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "AND s.storeAddress LIKE CONCAT('%', :roadName, '%')")
    Page<Store> findStoresByProductNameAndRoadName(@Param("keyword") String keyword,
                                                   @Param("roadName") String roadName,
                                                   Pageable pageable);
}

