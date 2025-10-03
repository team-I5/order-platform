package com.spartaclub.orderplatform.domain.product.repository;

import com.spartaclub.orderplatform.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
/**
 * 상품 Repository
 *
 * @author 류형선
 * @date 2025-10-02(목)
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    Page<Product> findByStore_StoreId(UUID storeId, Pageable pageable);
}
