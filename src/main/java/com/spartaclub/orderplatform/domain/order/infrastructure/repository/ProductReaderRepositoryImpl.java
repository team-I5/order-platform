package com.spartaclub.orderplatform.domain.order.infrastructure.repository;

import com.spartaclub.orderplatform.domain.order.domain.repository.ProductReaderRepository;
import com.spartaclub.orderplatform.domain.product.domain.entity.Product;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductReaderRepositoryImpl implements ProductReaderRepository {

//    private final ProductJpaRepository productJpaRepository;


    @Override
    public Product findById(UUID productId) {
        return null;
    }
}
// TODO: ProductJPARepository 생기면 수정하기
