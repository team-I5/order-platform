package com.spartaclub.orderplatform.domain.order.domain.repository;

import com.spartaclub.orderplatform.domain.product.domain.entity.Product;
import java.util.UUID;

public interface ProductReaderRepository {

    Product findById(UUID productId);
}
