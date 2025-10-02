package com.spartaclub.orderplatform.domain.product.service;

import com.spartaclub.orderplatform.domain.product.dto.ProductResponseDto;
import com.spartaclub.orderplatform.domain.product.dto.ProductCreateRequestDto;
import com.spartaclub.orderplatform.domain.product.dto.ProductUpdateRequestDto;
import com.spartaclub.orderplatform.domain.product.entity.Product;
import com.spartaclub.orderplatform.domain.product.mapper.ProductMapper;
import com.spartaclub.orderplatform.domain.product.repository.ProductRepository;
import com.spartaclub.orderplatform.domain.store.repository.StoreRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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

    // 상품 등록 서비스 로직
    @Transactional
    public ProductResponseDto createProduct(@Valid ProductCreateRequestDto productCreateRequestDto) {
        // 1. storeId로 Store 조회
//        Store store = storeRepository.findById(productRequestDto.getStoreId())
//                .orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));

        // 2. dto → entity 변환
        Product product = productMapper.toEntity(productCreateRequestDto);

        // 3. store 객체 연결
//        product.setStore(store);

        // 4. 저장
        Product savedProduct = productRepository.save(product);


        // 5. entity → dto 변환 후 반환
        return productMapper.toDto(savedProduct);
    }

    // 상품 수정 서비스 로직
    @Transactional
    public ProductResponseDto updateProduct(UUID productId, @Valid ProductUpdateRequestDto productUpdateRequestDto) {
        // 1. productId로 상품 조회
        Product product =  productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));

        // 2. 상품 정보 수정
        product.updateProduct(productUpdateRequestDto);

        // 3. JPA의 변경 감지(dirty checking)로 자동 저장
        // 별도로 save 호출 안 해도 @Transactional 안에서 commit 시 DB 반영

        // 4. entity -> dto 변환 후 반환
        return productMapper.toDto(product);
    }

    @Transactional
    public void deleteProduct(
            UUID productId
//            Long userId
    ) {
        // 1. productId로 상품 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));

        product.deleteProduct(0L); // 도메인 메서드 호출, 회원 연결 전 하드코딩
        // @Transactional 안에서 dirty checking으로 자동 반영
    }
}
