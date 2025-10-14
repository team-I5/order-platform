package com.spartaclub.orderplatform.domain.order.domain.repository;

import com.spartaclub.orderplatform.domain.product.domain.entity.Product;
import java.util.Optional;
import java.util.UUID;

public interface ProductReaderRepository {

    Optional<Product> findById(UUID productId);
}
