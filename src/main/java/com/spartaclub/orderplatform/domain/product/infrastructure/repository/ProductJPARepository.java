package com.spartaclub.orderplatform.domain.product.infrastructure.repository;

import com.spartaclub.orderplatform.domain.product.domain.entity.Product;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
/**
 * 상품 Repository
 *
 * @author 류형선
 * @date 2025-10-02(목)
 */
@Repository
public interface ProductJPARepository extends JpaRepository<Product, UUID> {
    Page<Product> findByStore_StoreIdAndIsHiddenFalseAndDeletedAtIsNull(UUID storeId, Pageable pageable);

    @Query("""
    SELECT DISTINCT p
    FROM Product p
    LEFT JOIN FETCH p.productOptionGroupMaps map
    LEFT JOIN FETCH map.productOptionGroup g
    LEFT JOIN FETCH g.optionItems i
    WHERE p.productId = :productId
""")
    Optional<Product> findWithOptionGroupsAndItemsByProductId(@Param("productId") UUID productId);

    List<Product> findByProductIdIn(Collection<UUID> productIds);
}
