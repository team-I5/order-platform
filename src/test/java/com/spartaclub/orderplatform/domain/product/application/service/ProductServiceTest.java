package com.spartaclub.orderplatform.domain.product.application.service;

import com.spartaclub.orderplatform.domain.ai.application.service.AiService;
import com.spartaclub.orderplatform.domain.product.application.mapper.ProductMapper;
import com.spartaclub.orderplatform.domain.product.domain.entity.Product;
import com.spartaclub.orderplatform.domain.product.domain.entity.ProductOptionGroup;
import com.spartaclub.orderplatform.domain.product.domain.repository.ProductRepository;
import com.spartaclub.orderplatform.domain.product.domain.repository.ProductStoreReaderRepository;
import com.spartaclub.orderplatform.domain.product.exception.ProductErrorCode;
import com.spartaclub.orderplatform.domain.product.presentation.dto.*;
import com.spartaclub.orderplatform.domain.review.domain.model.Review;
import com.spartaclub.orderplatform.domain.store.domain.model.Store;
import com.spartaclub.orderplatform.global.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

/**
 * ProductService 단위 테스트 (AssertJ + BDDMockito)
 */
@DisplayName("ProductService 단위 테스트")
class ProductServiceTest {


    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductStoreReaderRepository storeRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private AiService aiService;

    @InjectMocks
    private ProductService productService;

    private UUID storeId;
    private UUID productId;
    private Store store;
    private Product product;
    private ProductResponseDto productResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        storeId = UUID.randomUUID();
        productId = UUID.randomUUID();
        store = mock(Store.class);
        product = mock(Product.class);
        productResponse = mock(ProductResponseDto.class);
    }

    // ==============================================
    // 상품 등록 테스트
    // ==============================================
    @Nested
    @DisplayName("상품 등록 테스트")
    class CreateProductTest {

        @Test
        @DisplayName("상품 등록 성공")
        void createProduct_success() {
            // Given
            ProductCreateRequestDto request = new ProductCreateRequestDto(storeId, "치즈버거", 12000L, "맛있는 치즈버거", false);
            given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
            given(productRepository.save(any(Product.class))).willReturn(product);
            given(productMapper.toDto(product)).willReturn(productResponse);

            // When
            ProductResponseDto result = productService.createProduct(request, 1L);

            // Then
            assertThat(result).isNotNull();
            then(storeRepository).should(times(1)).findById(storeId);
            then(productRepository).should(times(1)).save(any(Product.class));
            then(aiService).should(times(1))
                    .saveAiLogsIfNeeded(eq(1L), any(UUID.class), any(Long.class), eq("맛있는 치즈버거"));
        }

        @Test
        @DisplayName("스토어가 존재하지 않을 때 상품 등록 실패")
        void createProduct_storeNotFound_fail() {
            // Given
            ProductCreateRequestDto request = new ProductCreateRequestDto(storeId, "치즈버거", 12000L, "맛있는 치즈버거", false);
            given(storeRepository.findById(storeId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> productService.createProduct(request, 1L))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .isEqualTo(ProductErrorCode.STORE_NOT_EXIST);
        }
    }

    // ==============================================
    // 상품 수정 테스트
    // ==============================================
    @Nested
    @DisplayName("상품 수정 테스트")
    class UpdateProductTest {

        @Test
        @DisplayName("상품 수정 성공")
        void updateProduct_success() {
            // Given
            ProductUpdateRequestDto request = new ProductUpdateRequestDto("불고기버거", 15000L, "맛있는 불고기버거");
            given(productRepository.findById(productId)).willReturn(Optional.of(product));
            given(productMapper.toDto(product)).willReturn(productResponse);

            // When
            ProductResponseDto result = productService.updateProduct(productId, request);

            // Then
            assertThat(result).isNotNull();
            then(product).should(times(1)).updateProduct(request);
        }

        @Test
        @DisplayName("상품이 존재하지 않을 때 수정 실패")
        void updateProduct_notFound_fail() {
            // Given
            ProductUpdateRequestDto request = new ProductUpdateRequestDto("불고기버거", 15000L, "맛있는 불고기버거");
            given(productRepository.findById(productId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> productService.updateProduct(productId, request))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .isEqualTo(ProductErrorCode.PRODUCT_NOT_EXIST);
        }
    }

    // ==============================================
    // 상품 삭제 테스트
    // ==============================================
    @Nested
    @DisplayName("상품 삭제 테스트")
    class DeleteProductTest {

        @Test
        @DisplayName("상품 삭제 성공")
        void deleteProduct_success() {
            // Given
            given(productRepository.findById(productId)).willReturn(Optional.of(product));

            // When
            productService.deleteProduct(1L, productId);

            // Then
            then(product).should(times(1)).deleteProduct(1L);
        }

        @Test
        @DisplayName("상품이 존재하지 않을 때 삭제 실패")
        void deleteProduct_notFound_fail() {
            // Given
            given(productRepository.findById(productId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> productService.deleteProduct(1L, productId))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .isEqualTo(ProductErrorCode.PRODUCT_NOT_EXIST);
        }
    }

    // =====================================================
    // 상품 상세 조회
    // =====================================================
    @Nested
    @DisplayName("상품 상세 조회 테스트")
    class GetProductDetailTest {

        @Test
        @DisplayName("상품 상세 조회 성공")
        void getProduct_success() {
            // Given
            given(productRepository.findWithOptionGroupsAndItemsByProductId(productId))
                    .willReturn(Optional.of(product));
            given(productMapper.toResponseDto(product)).willReturn(ProductDetailResponseDto);

            // When
            ProductDetailResponseDto result = productService.getProduct(productId);

            // Then
            assertThat(result).isNotNull();
            then(productRepository).should(times(1))
                    .findWithOptionGroupsAndItemsByProductId(productId);
        }

        @Test
        @DisplayName("상품 상세 조회 실패 - 존재하지 않는 상품")
        void getProduct_notFound_fail() {
            // Given
            given(productRepository.findWithOptionGroupsAndItemsByProductId(productId))
                    .willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> productService.getProduct(productId))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .isEqualTo(ProductErrorCode.PRODUCT_NOT_EXIST);
        }
    }

    // =====================================================
    // 상품 공개/숨김 전환
    // =====================================================
    @Nested
    @DisplayName("상품 공개/숨김 전환 테스트")
    class UpdateProductVisibilityTest {

        @Test
        @DisplayName("상품 공개/숨김 전환 성공")
        void updateProductVisibility_success() {
            // Given
            given(productRepository.findById(productId)).willReturn(Optional.of(product));
            ProductResponseDto responseDto = mock(ProductResponseDto.class);
            given(productMapper.toDto(product)).willReturn(responseDto);

            // When
            ProductResponseDto result = productService.updateProductVisibility(productId);

            // Then
            assertThat(result).isNotNull();
            then(product).should(times(1)).updateVisibility();
        }

        @Test
        @DisplayName("상품 공개/숨김 전환 실패 - 존재하지 않는 상품")
        void updateProductVisibility_notFound_fail() {
            // Given
            given(productRepository.findById(productId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> productService.updateProductVisibility(productId))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .isEqualTo(ProductErrorCode.PRODUCT_NOT_EXIST);
        }
    }

    // =====================================================
    // 상품명 + 배송지 기반 상점 검색
    // =====================================================
    @Nested
    @DisplayName("상품명 + 배송지 기반 상점 검색 테스트")
    class GetStoreListByProductNameAndAddressTest {

        @Test
        @DisplayName("상품명과 배송지 기반 상점 검색 성공")
        void getStoreList_success() {
            // Given
            String keyword = "치킨";
            Address address = new Address();
            address.setRoadNameAddress("서울특별시 노원구 한글비석로 24");
            given(addressRepository.findById(addressId)).willReturn(Optional.of(address));

            Store store = mock(Store.class);
            Page<Store> storePage = new PageImpl<>(List.of(store));
            given(storeRepository.findDistinctByProductNameContainingIgnoreCase(eq(keyword), anyString(), any(Pageable.class)))
                    .willReturn(storePage);

            ProductStoreSearchResponseDto responseDto = mock(ProductStoreSearchResponseDto.class);
            given(productMapper.toProductStoreSearchResponseDto(store)).willReturn(responseDto);
            given(productMapper.toPageDto(storePage)).willReturn(mock(PageMetaDto.class));

            // When
            PageResponseDto<ProductStoreSearchResponseDto> result =
                    productService.getStoreListByProductNameAndAddressId(keyword, addressId, PageRequest.of(0, 10));

            // Then
            assertThat(result.getData()).isNotEmpty();
            then(addressRepository).should(times(1)).findById(addressId);
            then(storeRepository).should(times(1))
                    .findDistinctByProductNameContainingIgnoreCase(eq(keyword), anyString(), any(Pageable.class));
        }

        @Test
        @DisplayName("배송지 주소가 존재하지 않을 때 실패")
        void getStoreList_addressNotFound_fail() {
            // Given
            given(addressRepository.findById(addressId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() ->
                    productService.getStoreListByProductNameAndAddressId("치킨", addressId, PageRequest.of(0, 10)))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .isEqualTo(ProductErrorCode.ADDRESS_NOT_EXIST);
        }
    }

    // =====================================================
    // 상품에 옵션 그룹 추가
    // =====================================================
    @Nested
    @DisplayName("상품에 옵션 그룹 추가 테스트")
    class AddGroupToProductTest {

        @Test
        @DisplayName("상품에 옵션 그룹 추가 성공")
        void addGroupToProduct_success() {
            // Given
            UUID optionGroupId = UUID.randomUUID();
            ProductAddOptionGroupsRequestDto request =
                    new ProductAddOptionGroupsRequestDto(productId, List.of(optionGroupId));

            ProductOptionGroup optionGroup = mock(ProductOptionGroup.class);
            given(productRepository.findById(productId)).willReturn(Optional.of(product));
            given(productOptionGroupRepository.findById(optionGroupId)).willReturn(Optional.of(optionGroup));

            // When
            productService.addGroupToProduct(request);

            // Then
            then(product).should(times(1)).addOptionGroup(optionGroup);
            then(productRepository).should(times(1)).save(product);
        }

        @Test
        @DisplayName("존재하지 않는 상품일 때 실패")
        void addGroupToProduct_productNotFound_fail() {
            // Given
            UUID optionGroupId = UUID.randomUUID();
            ProductAddOptionGroupsRequestDto request =
                    new ProductAddOptionGroupsRequestDto(productId, List.of(optionGroupId));
            given(productRepository.findById(productId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> productService.addGroupToProduct(request))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .isEqualTo(ProductErrorCode.PRODUCT_NOT_EXIST);
        }

        @Test
        @DisplayName("존재하지 않는 옵션 그룹일 때 실패")
        void addGroupToProduct_optionGroupNotFound_fail() {
            // Given
            UUID optionGroupId = UUID.randomUUID();
            ProductAddOptionGroupsRequestDto request =
                    new ProductAddOptionGroupsRequestDto(productId, List.of(optionGroupId));
            given(productRepository.findById(productId)).willReturn(Optional.of(product));
            given(productOptionGroupRepository.findById(optionGroupId)).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> productService.addGroupToProduct(request))
                    .isInstanceOf(BusinessException.class)
                    .extracting("errorCode")
                    .isEqualTo(ProductErrorCode.PRODUCT_OPTION_GROUP_NOT_EXIST);
        }
    }

    // =====================================================
    // 상품 리뷰 리스트 조회
    // =====================================================
    @Nested
    @DisplayName("상품 리뷰 리스트 조회 테스트")
    class GetReviewListTest {

        @Test
        @DisplayName("상품 리뷰 리스트 조회 성공")
        void getReviewList_success() {
            // Given
            Review review = mock(Review.class);
            Page<Review> reviewPage = new PageImpl<>(List.of(review));
            given(reviewRepository.findAllByProduct_ProductId(eq(productId), any(Pageable.class)))
                    .willReturn(reviewPage);

            ProductReviewResponseDto reviewDto = mock(ProductReviewResponseDto.class);
            given(productMapper.toReviewDto(review)).willReturn(reviewDto);
            given(productMapper.toPageDto(reviewPage)).willReturn(mock(PageMetaDto.class));

            // When
            PageResponseDto<ProductReviewResponseDto> result =
                    productService.getReviewListByProductId(productId, PageRequest.of(0, 5));

            // Then
            assertThat(result.getData()).isNotEmpty();
            then(reviewRepository).should(times(1))
                    .findAllByProduct_ProductId(eq(productId), any(Pageable.class));
        }
    }
}
}
