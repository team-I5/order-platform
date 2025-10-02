package com.spartaclub.orderplatform.domain.product.service;

import com.spartaclub.orderplatform.domain.product.dto.ProductRequestDto;
import com.spartaclub.orderplatform.domain.product.dto.ProductResponseDto;
import com.spartaclub.orderplatform.domain.product.entity.Product;
import com.spartaclub.orderplatform.domain.product.mapper.ProductMapper;
import com.spartaclub.orderplatform.domain.product.repository.ProductRepository;
import com.spartaclub.orderplatform.domain.store.entity.Store;
import com.spartaclub.orderplatform.domain.store.repository.StoreRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 상품 Service
 *
 * @author 류형선
 * @date 2025-10-02(목)
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final StoreRepository storeRepository;

    @Transactional
    public ProductResponseDto createProduct(@Valid @RequestBody ProductRequestDto productRequestDto) {
        // 1. storeId로 Store 조회
//        Store store = storeRepository.findById(productRequestDto.getStoreId())
//                .orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));

        // 2. dto → entity 변환
        Product product = productMapper.toEntity(productRequestDto);

        // 3. store 객체 연결
//        product.setStore(null);

        // 4. 저장
        Product savedProduct = productRepository.save(product);


        // 5. entity → dto 변환
        return productMapper.toDto(savedProduct);
    }
}
