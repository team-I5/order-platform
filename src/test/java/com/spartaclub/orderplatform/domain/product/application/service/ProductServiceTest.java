package com.spartaclub.orderplatform.domain.product.application.service;

import com.spartaclub.orderplatform.domain.ai.application.service.AiService;
import com.spartaclub.orderplatform.domain.product.application.mapper.ProductMapper;
import com.spartaclub.orderplatform.domain.product.domain.entity.Product;
import com.spartaclub.orderplatform.domain.product.domain.entity.ProductOptionGroup;
import com.spartaclub.orderplatform.domain.product.domain.repository.*;
import com.spartaclub.orderplatform.domain.product.exception.ProductErrorCode;
import com.spartaclub.orderplatform.domain.product.presentation.dto.*;
import com.spartaclub.orderplatform.domain.review.domain.model.Review;
import com.spartaclub.orderplatform.domain.store.domain.model.Store;
import com.spartaclub.orderplatform.domain.user.domain.entity.Address;
import com.spartaclub.orderplatform.global.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@DisplayName("ProductService 단위 테스트")
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private ProductStoreReaderRepository storeRepository;
    @Mock
    private ProductAddressReaderRepository addressRepository;
    @Mock
    private AiService aiService;
    @Mock
    private ProductOptionGroupRepository productOptionGroupRepository;
    @Mock
    private ProductReviewReaderRepository reviewRepository;

    // 공통 테스트 데이터
    private Long createdId;
    private Long userId;
    private UUID storeId;
    private UUID productId;
    private UUID addressId;
    private UUID optionGroupId;

    private Store store;
    private Product product;
    private Address address;
    private ProductOptionGroup optionGroup;
    private Review review;

    private ProductCreateRequestDto productCreateRequestDto;
    private ProductUpdateRequestDto updateRequestDto;

    private ProductResponseDto productResponseDto;
    private ProductStoreSearchResponseDto productStoreSearchResponseDto;
    private ProductDetailResponseDto productDetailResponseDto;
    private ProductReviewResponseDto productReviewResponseDto;

    private Pageable pageable;
    private PageMetaDto pageMetaDto;
    private Page<Product> productPage;
    private Page<Store> storePage;
    private Page<Review> reviewPage;

    @BeforeEach
    void setUp() {
        createdId = 1L;
        userId = 1L;
        storeId = UUID.randomUUID();
        productId = UUID.randomUUID();
        addressId = UUID.randomUUID();
        optionGroupId = UUID.randomUUID();

        store = mock(Store.class);
        product = mock(Product.class);
        address = mock(Address.class);
        optionGroup = mock(ProductOptionGroup.class);
        review = mock(Review.class);

        productCreateRequestDto = ProductCreateRequestDto.builder()
                .productName("테스트상품")
                .price(1000L)
                .productDescription("테스트상품 설명")
                .storeId(storeId)
                .build();
        updateRequestDto = ProductUpdateRequestDto.builder()
                .productName("수정상품")
                .price(2000L)
                .productDescription("수정상품 설명")
                .build();

        productResponseDto = mock(ProductResponseDto.class);
        productDetailResponseDto = mock(ProductDetailResponseDto.class);
        productStoreSearchResponseDto = mock(ProductStoreSearchResponseDto.class);
        productReviewResponseDto = mock(ProductReviewResponseDto.class);

        pageable = Pageable.ofSize(10); // 테스트용 페이지 객체
        pageMetaDto = mock(PageMetaDto.class);
        productPage = mock(Page.class);
        storePage = mock(Page.class);
        reviewPage = mock(Page.class);
    }

    @Nested
    @DisplayName("상품 생성 기능 테스트")
    class CreateProductTest {

        @Test
        @DisplayName("상품 생성 성공 시 DTO 반환")
        void createProduct_success() {
            // given
            given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
            given(productRepository.save(any(Product.class))).willReturn(product);
            given(product.getProductId()).willReturn(productId);
            given(productMapper.toDto(product)).willReturn(productResponseDto);

            // when
            ProductResponseDto result = productService.createProduct(productCreateRequestDto, createdId);

            // then
            assertThat(result).isEqualTo(productResponseDto);
            then(aiService).should().saveOrUpdateAiLogs(userId, productId, "테스트상품 설명", false);
        }

        @Test
        @DisplayName("상품 생성 실패 - 상점이 존재하지 않음")
        void createProduct_storeNotExist() {
            // given
            ProductCreateRequestDto requestDto = ProductCreateRequestDto.builder()
                    .storeId(storeId)
                    .build();

            given(storeRepository.findById(storeId)).willReturn(Optional.empty());

            // when & then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> productService.createProduct(requestDto, createdId));

            assertThat(exception.getErrorCode()).isEqualTo(ProductErrorCode.STORE_NOT_EXIST);
        }
    }

    @Nested
    @DisplayName("상품 수정 기능 테스트")
    class UpdateProductTest {

        @Test
        @DisplayName("상품 수정 성공")
        void updateProduct_success() {
            // given
            given(productRepository.findById(productId)).willReturn(Optional.of(product));
            given(productMapper.toDto(product)).willReturn(productResponseDto);

            // when
            ProductResponseDto result = productService.updateProduct(userId, productId, updateRequestDto);

            // then
            then(product).should().updateProduct(updateRequestDto); // 도메인 메서드 호출 확인
            assertThat(result).isEqualTo(productResponseDto);
            then(aiService).should().saveOrUpdateAiLogs(userId, productId, "수정상품 설명", true);
        }

        @Test
        @DisplayName("상품 수정 실패 - 상품 존재하지 않음")
        void updateProduct_productNotExist() {
            // given
            given(productRepository.findById(productId)).willReturn(Optional.empty());

            // when & then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> productService.updateProduct(userId, productId, updateRequestDto));

            assertThat(exception.getErrorCode()).isEqualTo(ProductErrorCode.PRODUCT_NOT_EXIST);
        }
    }

    @Nested
    @DisplayName("상품 삭제 기능 테스트")
    class DeleteProductTest {

        @Test
        @DisplayName("상품 삭제 성공")
        void deleteProduct_success() {
            // given
            given(productRepository.findById(productId)).willReturn(Optional.of(product));

            // when
            productService.deleteProduct(userId, productId);

            // then
            then(product).should().deleteProduct(userId);
        }

        @Test
        @DisplayName("상품 삭제 실패 - 상품 존재하지 않음")
        void updateProduct_productNotExist() {
            // given
            given(productRepository.findById(productId)).willReturn(Optional.empty());

            // when & then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> productService.deleteProduct(userId, productId));

            assertThat(exception.getErrorCode()).isEqualTo(ProductErrorCode.PRODUCT_NOT_EXIST);
        }
    }

    @Nested
    @DisplayName("상품 목록 조회 기능 테스트")
    class GetProductListTest {

        @Test
        @DisplayName("상품 목록 조회 성공")
        void getProductList_success() {
            // given
            Product product1 = mock(Product.class);
            Product product2 = mock(Product.class);

            ProductResponseDto dto1 = mock(ProductResponseDto.class);
            ProductResponseDto dto2 = mock(ProductResponseDto.class);

            List<Product> products = List.of(product1, product2);
            List<ProductResponseDto> dtos = List.of(dto1, dto2);

            given(productRepository.findByStore_StoreIdAndIsHiddenFalseAndDeletedAtIsNull(storeId, pageable))
                    .willReturn(productPage);
            given(productPage.getContent()).willReturn(products);
            given(productMapper.toDto(product1)).willReturn(dto1);
            given(productMapper.toDto(product2)).willReturn(dto2);

            PageMetaDto pageMetaDto = mock(PageMetaDto.class);
            given(productMapper.toPageDto(productPage)).willReturn(pageMetaDto);

            // when
            PageResponseDto<ProductResponseDto> result = productService.getProductList(storeId, pageable);

            // then
            assertThat(result.getContent()).isEqualTo(dtos);
            assertThat(result.getMeta()).isEqualTo(pageMetaDto);
        }

        @Test
        @DisplayName("조회된 상품이 없을 때 빈 리스트 반환")
        void getProductList_empty() {
            // given
            given(productPage.getContent()).willReturn(List.of());
            given(productMapper.toPageDto(productPage)).willReturn(mock(PageMetaDto.class));
            given(productRepository.findByStore_StoreIdAndIsHiddenFalseAndDeletedAtIsNull(storeId, pageable))
                    .willReturn(productPage);

            // when
            PageResponseDto<ProductResponseDto> result = productService.getProductList(storeId, pageable);

            // then
            assertThat(result.getContent()).isEmpty();
        }

        @Test
        @DisplayName("Pageable이 null일 때 NPE 발생")
        void getProductList_pageableNull() {
            // when & then
            assertThrows(NullPointerException.class, () -> productService.getProductList(storeId, null));
        }
    }

    @Nested
    @DisplayName("상품 상세 조회 기능 테스트")
    class GetProductDetailTest {

        @Test
        @DisplayName("상품 상세 조회 성공")
        void getProduct_success() {
            // given
            given(productRepository.findWithOptionGroupsAndItemsByProductId(productId))
                    .willReturn(Optional.of(product));
            given(productMapper.toResponseDto(product)).willReturn(productDetailResponseDto);

            // when
            ProductDetailResponseDto result = productService.getProduct(productId);

            // then
            assertThat(result).isEqualTo(productDetailResponseDto);
        }

        @Test
        @DisplayName("상품 상세 조회 실패 - 상품 존재하지 않음")
        void getProduct_productNotExist() {
            // given
            given(productRepository.findWithOptionGroupsAndItemsByProductId(productId))
                    .willReturn(Optional.empty());

            // when & then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> productService.getProduct(productId));

            assertThat(exception.getErrorCode()).isEqualTo(ProductErrorCode.PRODUCT_NOT_EXIST);
        }
    }

    @Nested
    @DisplayName("상품 공개/숨김 수정 기능 테스트")
    class UpdateProductVisibilityTest {

        @Test
        @DisplayName("상품 공개/숨김 수정 성공")
        void updateProductVisibility_success() {
            // given
            given(productRepository.findById(productId)).willReturn(Optional.of(product));
            given(productMapper.toDto(product)).willReturn(productResponseDto);

            // when
            ProductResponseDto result = productService.updateProductVisibility(productId);

            // then
            then(product).should().updateVisibility(); // 도메인 메서드 호출 검증
            assertThat(result).isEqualTo(productResponseDto);
        }

        @Test
        @DisplayName("상품 공개/숨김 수정 실패 - 상품 존재하지 않음")
        void updateProductVisibility_productNotExist() {
            // given
            given(productRepository.findById(productId)).willReturn(Optional.empty());

            // when & then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> productService.updateProductVisibility(productId));

            assertThat(exception.getErrorCode()).isEqualTo(ProductErrorCode.PRODUCT_NOT_EXIST);
        }
    }

    @Nested
    @DisplayName("상품 연관 상점 검색 기능 테스트")
    class GetStoreListByProductNameAndAddressIdTest {

        private String keyword;

        @BeforeEach
        void setUpStoreSearch() {
            keyword = "테스트";
        }

        @Test
        @DisplayName("상품 연관 상점 검색 성공")
        void getStoreList_success() {
            // given
            given(addressRepository.findById(addressId)).willReturn(Optional.of(address));
            given(address.getRoadNameAddress()).willReturn("서울특별시 노원구 한글비석로 24");
            given(storeRepository.findDistinctByProductNameContainingIgnoreCase(keyword, "한글비석로", pageable))
                    .willReturn(storePage);
            given(storePage.getContent()).willReturn(List.of(store));
            given(productMapper.toProductStoreSearchResponseDto(store)).willReturn(productStoreSearchResponseDto);
            given(productMapper.toPageDto(storePage)).willReturn(pageMetaDto);

            // when
            PageResponseDto<ProductStoreSearchResponseDto> result = productService
                    .getStoreListByProductNameAndAddressId(keyword, addressId, pageable);

            // then
            assertThat(result.getContent()).containsExactly(productStoreSearchResponseDto);
            assertThat(result.getMeta()).isEqualTo(pageMetaDto);
        }

        @Test
        @DisplayName("상품 연관 상점 검색 실패 - 주소 존재하지 않음")
        void getStoreList_addressNotExist() {
            // given
            given(addressRepository.findById(addressId)).willReturn(Optional.empty());

            // when & then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> productService.getStoreListByProductNameAndAddressId(keyword, addressId, pageable));

            assertThat(exception.getErrorCode()).isEqualTo(ProductErrorCode.ADDRESS_NOT_EXIST);
        }

        @Test
        @DisplayName("주소에서 도로명 추출 실패 시 빈 문자열 반환")
        void extractRoadName_notFound() {
            // given
            given(addressRepository.findById(addressId)).willReturn(Optional.of(address));
            given(address.getRoadNameAddress()).willReturn("서울특별시 노원구 123"); // "로"나 "길" 없음
            given(storeRepository.findDistinctByProductNameContainingIgnoreCase(keyword, "", pageable))
                    .willReturn(storePage);
            given(storePage.getContent()).willReturn(List.of());
            given(productMapper.toPageDto(storePage)).willReturn(mock(PageMetaDto.class));

            // when
            PageResponseDto<ProductStoreSearchResponseDto> result = productService
                    .getStoreListByProductNameAndAddressId(keyword, addressId, pageable);

            // then
            assertThat(result.getContent()).isEmpty(); // 데이터 없음
        }

        @Test
        @DisplayName("조회된 상점이 없는 경우 빈 리스트 반환")
        void getStoreList_noStores() {
            // given
            given(addressRepository.findById(addressId)).willReturn(Optional.of(address));
            given(address.getRoadNameAddress()).willReturn("서울특별시 노원구 한글비석로 24");
            given(storeRepository.findDistinctByProductNameContainingIgnoreCase(keyword, "한글비석로", pageable))
                    .willReturn(storePage);
            given(storePage.getContent()).willReturn(List.of());
            given(productMapper.toPageDto(storePage)).willReturn(mock(PageMetaDto.class));

            // when
            PageResponseDto<ProductStoreSearchResponseDto> result = productService
                    .getStoreListByProductNameAndAddressId(keyword, addressId, pageable);

            // then
            assertThat(result.getContent()).isEmpty(); // 조회 결과 없음
        }
    }


    @Nested
    @DisplayName("상품 옵션 그룹 추가 기능 테스트")
    class AddOptionGroupTest {

        @Test
        @DisplayName("상품에 옵션 그룹 추가 성공")
        void addGroupToProduct_success() {
            // given
            given(productRepository.findById(productId)).willReturn(Optional.of(product));
            given(productOptionGroupRepository.findById(optionGroupId)).willReturn(Optional.of(optionGroup));

            ProductAddOptionGroupsRequestDto requestDto = ProductAddOptionGroupsRequestDto.builder()
                    .productId(productId)
                    .productOptionGroupIds(List.of(optionGroupId))
                    .build();

            // when
            productService.addGroupToProduct(requestDto);

            // then
            then(product).should().addOptionGroup(optionGroup);
            then(productRepository).should().save(product);
        }

        @Test
        @DisplayName("상품에 옵션 그룹 추가 실패 - 옵션 그룹이 존재하지 않음")
        void addGroupToProduct_optionGroupNotExist() {
            // given
            given(productRepository.findById(productId)).willReturn(Optional.of(product));
            given(productOptionGroupRepository.findById(optionGroupId)).willReturn(Optional.empty());

            ProductAddOptionGroupsRequestDto requestDto = ProductAddOptionGroupsRequestDto.builder()
                    .productId(productId)
                    .productOptionGroupIds(List.of(optionGroupId))
                    .build();

            // when & then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> productService.addGroupToProduct(requestDto));

            assertThat(exception.getErrorCode()).isEqualTo(ProductErrorCode.PRODUCT_OPTION_GROUP_NOT_EXIST);
        }
    }

    @Nested
    @DisplayName("상품 리뷰 조회 기능 테스트")
    class GetReviewListByProductIdTest {

        @Test
        @DisplayName("상품 리뷰 조회 성공")
        void getReviewList_success() {
            // given
            given(reviewRepository.findAllByProduct_ProductId(productId, pageable)).willReturn(reviewPage);
            given(reviewPage.getContent()).willReturn(List.of(review));
            given(productMapper.toReviewDto(review)).willReturn(productReviewResponseDto);
            given(productMapper.toPageDto(reviewPage)).willReturn(pageMetaDto);

            // when
            PageResponseDto<ProductReviewResponseDto> result = productService
                    .getReviewListByProductId(productId, pageable);

            // then
            assertThat(result.getContent()).containsExactly(productReviewResponseDto);
            assertThat(result.getMeta()).isEqualTo(pageMetaDto);
        }

        @Test
        @DisplayName("상품 리뷰 조회 실패 - 리뷰 없음")
        void getReviewList_noReviews() {
            // given
            given(reviewRepository.findAllByProduct_ProductId(productId, pageable)).willReturn(reviewPage);
            given(reviewPage.getContent()).willReturn(List.of());
            given(productMapper.toPageDto(reviewPage)).willReturn(mock(PageMetaDto.class));

            // when
            PageResponseDto<ProductReviewResponseDto> result = productService
                    .getReviewListByProductId(productId, pageable);

            // then
            assertThat(result.getContent()).isEmpty(); // 리뷰 리스트 비어있음
        }
    }


}
