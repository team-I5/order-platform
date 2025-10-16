package com.spartaclub.orderplatform.domain.product.application.service;

import com.spartaclub.orderplatform.domain.ai.application.service.AiService;
import com.spartaclub.orderplatform.domain.product.application.mapper.ProductMapper;
import com.spartaclub.orderplatform.domain.product.domain.entity.Product;
import com.spartaclub.orderplatform.domain.product.domain.entity.ProductOptionGroup;
import com.spartaclub.orderplatform.domain.product.domain.repository.ProductOptionGroupRepository;
import com.spartaclub.orderplatform.domain.product.domain.repository.ProductRepository;
import com.spartaclub.orderplatform.domain.product.domain.repository.ReviewReaderRepository;
import com.spartaclub.orderplatform.domain.product.domain.repository.ProductStoreReaderRepository;
import com.spartaclub.orderplatform.domain.product.presentation.dto.*;
import com.spartaclub.orderplatform.domain.review.entity.Review;
import com.spartaclub.orderplatform.domain.store.application.mapper.StoreMapper;
import com.spartaclub.orderplatform.domain.store.domain.model.Store;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreSearchResponseDto;
import com.spartaclub.orderplatform.domain.user.domain.entity.Address;
import com.spartaclub.orderplatform.domain.user.domain.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

/**
 * 상품 Service
 *
 * @author 류형선
 * @date 2025-10-11(토)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductStoreReaderRepository storeRepository;
    private final StoreMapper storeMapper;
    private final AddressRepository addressRepository;
    private final AiService aiService;
    private final ProductOptionGroupRepository productOptionGroupRepository;
    private final ReviewReaderRepository reviewRepository;

    // 상품 등록 서비스 로직
    @Transactional
    public ProductResponseDto createProduct(
            ProductCreateRequestDto productCreateRequestDto,
            Long userId
    ) {
        // 1. storeId로 Store 조회
        Store store = storeRepository.findById(productCreateRequestDto.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));

        // 2. dto → entity 변환
        Product product = productMapper.toEntity(productCreateRequestDto);

        // 3. store 객체 연결
        product.setStore(store);

        // 4. 저장
        Product savedProduct = productRepository.save(product);

        // 5. 캐시에 AI 응답이 있으면 로그 저장
        aiService.saveAiLogsIfNeeded(userId, savedProduct.getProductId(), savedProduct.getCreatedId(), productCreateRequestDto.getProductDescription());

        // 6. entity → dto 변환 후 반환
        return productMapper.toDto(savedProduct);
    }

    // 상품 수정 서비스 로직
    @Transactional
    public ProductResponseDto updateProduct(UUID productId,
                                            ProductUpdateRequestDto productUpdateRequestDto
    ) {
        // 1. productId로 상품 조회
        Product product = findProductOrThrow(productId);

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
            Long userId,
            UUID productId
    ) {
        // 1. productId로 상품 조회
        Product product = findProductOrThrow(productId);

        product.deleteProduct(userId); // 도메인 메서드 호출, 회원 연결 전 하드코딩
        // @Transactional 안에서 dirty checking으로 자동 반영
    }

    // 상품 목록 조회 서비스 로직
    public PageResponseDto<ProductResponseDto> getProductList(UUID storeId, Pageable pageable) {
        // 1. storeId로 상품 리스트 페이지 객체로 조회( isHidden이 false 이고, deletedAt이 NULL인 상품만 조회)
        Page<Product> productPage = productRepository.findByStore_StoreIdAndIsHiddenFalseAndDeletedAtIsNull(storeId, pageable);

        // 2. 페이지 객체에서 상품 리스트만 추출
        List<ProductResponseDto> productList = productPage.getContent().stream()
                .map(productMapper::toDto)
                .toList();

        // 3. 페이지 메타 데이터 -> dto 변환
        PageMetaDto pageMetaDto = productMapper.toPageDto(productPage);

        // 4. 상품 리스트와 메타 데이터 dto 반환
        return new PageResponseDto<>(productList, pageMetaDto);
    }

    // 상품 상세 조회 서비스 로직
    public ProductDetailResponseDto getProduct(UUID productId) {
        // 1. productId로 상품 조회
        Product product = productRepository.findWithOptionGroupsAndItemsByProductId(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        return productMapper.toResponseDto(product);
    }

    // 상품 공개/숨김 수정 서비스 로직
    @Transactional
    public ProductResponseDto updateProductVisibility(UUID productId) {
        // 1. productId로 상품 조회
        Product product = findProductOrThrow(productId);

        // 2. isHidden 속성 수정
        product.updateVisibility();

        // 3. JPA의 변경 감지(dirty checking)로 자동 저장
        // 별도로 save 호출 안 해도 @Transactional 안에서 commit 시 DB 반영

        // 4. Product Entity -> Dto 변환 후 반환
        return productMapper.toDto(product);
    }


    // 검색 키워드와 사용자 배송지 정보로 상점 검색
    public Page<StoreSearchResponseDto> getStoreListByProductNameAndAddressId(String keyword, UUID addressId, Pageable pageable) {
        // 1. 사용자의 배송지 조회
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "배송지가 존재하지 않습니다."));

        // 2. 도로명 주소만 추출
        String roadName = extractRoadName(address.getRoadNameAddress());

        // 3. 키워드로 찾은 상품과 연계된 가게 중 배송지 주소 근처인 가게 조회
        Page<Store> storePage = storeRepository.findDistinctByProductNameContainingIgnoreCase(keyword, roadName, pageable);

        // 3. entity -> dto 후 반환
        return storePage.map(storeMapper::toStoreSearchResponseDto);
    }


    @Transactional
    public void addGroupToProduct(ProductAddOptionGroupsRequestDto productAddOptionGroupsRequestDto) {
        // 1. 상품 조회
        Product product = findProductOrThrow(productAddOptionGroupsRequestDto.getProductId());

        // 2. 상품 옵션 그룹들 조회
        List<ProductOptionGroup> optionGroups = productAddOptionGroupsRequestDto.getProductOptionGroupIds().stream()
                .map(productOptionGroupId -> productOptionGroupRepository.findById(productOptionGroupId)
                        .orElseThrow(() -> new IllegalArgumentException("상품 옵션 그룹을 찾을 수 없습니다: " + productOptionGroupId)))
                .toList();

        // 3. 상품에 상품 옵션 그룹 매핑
        optionGroups.forEach(product::addOptionGroup);

        // 4. 저장
        productRepository.save(product);
    }

    public PageResponseDto<ProductReviewResponseDto> getReviewListByProductId(UUID productId, Pageable pageable) {
        Page<Review> reviewPage =  reviewRepository.findAllByProduct_ProductId(productId, pageable);

        // 2. 페이지 객체에서 상품 리스트만 추출
        List<ProductReviewResponseDto> reviewList = reviewPage.getContent().stream()
                .map(productMapper::toReviewDto)
                .toList();

        // 3. 페이지 메타 데이터 -> dto 변환
        PageMetaDto pageMetaDto = productMapper.toPageDto(reviewPage);

        // 4. 상품 리스트와 메타 데이터 dto 반환
        return new PageResponseDto<>(reviewList, pageMetaDto);
    }


    // --- 상품 공통 조회 메소드 ---
    private Product findProductOrThrow(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "상품이 존재하지 않습니다."));
    }

    // 사용자 배송지에서 도로명 주소만 추출하는 메소드
    private String extractRoadName(String roadAddress) {
        // 예: "서울특별시 노원구 한글비석로 24" → "한글비석로"
        String[] parts = roadAddress.split(" ");
        for (String part : parts) {
            if (part.endsWith("로") || part.endsWith("길")) {
                return part;
            }
        }
        return ""; // 못 찾은 경우
    }

//    public Page<ProductReviewResponseDto> getReviewListByProductId(UUID productId, Pageable pageable) {
//    }
}
