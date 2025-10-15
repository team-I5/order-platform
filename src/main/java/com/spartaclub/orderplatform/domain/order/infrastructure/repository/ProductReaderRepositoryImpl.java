package com.spartaclub.orderplatform.domain.order.infrastructure.repository;

import com.spartaclub.orderplatform.domain.order.domain.repository.ProductReaderRepository;
import com.spartaclub.orderplatform.domain.product.domain.entity.Product;
import com.spartaclub.orderplatform.domain.product.infrastructure.repository.ProductRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductReaderRepositoryImpl implements ProductReaderRepository {

    private final ProductRepository productRepository;

    @Override
    public Optional<Product> findById(UUID productId) {
        return productRepository.findById(productId);
    }
}
// TODO: ProductJPARepository 생기면 수정하기
