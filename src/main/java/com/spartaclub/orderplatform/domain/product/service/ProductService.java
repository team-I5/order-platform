package com.spartaclub.orderplatform.domain.product.service;

import com.spartaclub.orderplatform.domain.product.dto.*;
import com.spartaclub.orderplatform.domain.product.entity.Product;
import com.spartaclub.orderplatform.domain.product.mapper.ProductMapper;
import com.spartaclub.orderplatform.domain.product.repository.ProductRepository;
import com.spartaclub.orderplatform.domain.store.repository.StoreRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 상품 Service
 *
 * @author 류형선
 * @date 2025-10-02(목)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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

    // 상품 삭제 서비스 로직
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

    // 상품 목록 조회 서비스 로직
    public PageResponseDto<ProductResponseDto> getProductList(UUID storeId, Pageable pageable) {
        // 1. storeId로 상품 리스트 페이지 객체로 조회
        Page<Product> productPage = productRepository.findByStore_StoreId(storeId, pageable);

        // 2. 페이지 객체에서 삼품 리스트만 추출
        List<ProductResponseDto> productList = productPage.getContent().stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());

        // 3. 페이지 메타 데이터 -> dto 변환
        PageMetaDto pageMetaDto = PageMetaDto.builder()
                .pageNumber(productPage.getNumber())
                .pageSize(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .isFirst(productPage.isFirst())
                .isLast(productPage.isLast())
                .build();

        // 4. 상품 리스트와 메타 데이터 dto 반환
        return new PageResponseDto<>(productList, pageMetaDto);
    }

    // 상품 상세 조회 서비스 로직
    public ProductResponseDto getProduct(UUID productId) {
        // 1. productId로 상품 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));

        // 2. Product Entity -> Dto 변환 후 반환
        return productMapper.toDto(product);
    }

    // 상품 공개/숨김 수정 서비스 로직
    @Transactional
    public ProductResponseDto updateProductVisibility(UUID productId) {
        // 1. productId로 상품 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));

        // 2. isHidden 속성 수정
        product.updateVisibility();

        // 3. JPA의 변경 감지(dirty checking)로 자동 저장
        // 별도로 save 호출 안 해도 @Transactional 안에서 commit 시 DB 반영

        // 4. Product Entity -> Dto 변환 후 반환
        return productMapper.toDto(product);
    }
}
