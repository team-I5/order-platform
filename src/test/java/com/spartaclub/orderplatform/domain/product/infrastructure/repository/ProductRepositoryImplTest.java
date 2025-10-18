package com.spartaclub.orderplatform.domain.product.infrastructure.repository;

import com.spartaclub.orderplatform.domain.product.domain.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductRepositoryImpl 단위 테스트 (Mock)")
class ProductRepositoryImplTest {

    @Mock
    private ProductJPARepository productJPARepository;

    @InjectMocks
    private ProductRepositoryImpl productRepository;

    private UUID storeId;
    private UUID productId1;

    @BeforeEach
    void setUp() {
        storeId = UUID.randomUUID();
        productId1 = UUID.randomUUID();
    }

    // --------------------------------------------------------------------
    @Nested
    @DisplayName("save() - 상품 저장")
    class SaveTest {
        @Test
        @DisplayName("상품이 정상적으로 저장된다")
        void saveProduct() {
            Product product = mock(Product.class);
            given(product.getProductName()).willReturn("상품1");
            given(productJPARepository.save(product)).willReturn(product);

            Product savedProduct = productRepository.save(product);

            assertThat(savedProduct).isNotNull();
            assertThat(savedProduct.getProductName()).isEqualTo("상품1");
            then(productJPARepository).should(times(1)).save(product);
        }
    }

    // --------------------------------------------------------------------
    @Nested
    @DisplayName("findByStore_StoreIdAndIsHiddenFalseAndDeletedAtIsNull() - 매장별 상품 조회")
    class FindByStoreTest {
        @Test
        @DisplayName("매장 ID로 상품 목록 조회")
        void findByStoreId() {
            Product product1 = mock(Product.class);
            given(product1.getProductName()).willReturn("상품1");
            Product product2 = mock(Product.class);
            given(product2.getProductName()).willReturn("상품2");

            Page<Product> page = new PageImpl<>(List.of(product1, product2));
            given(productJPARepository.findByStore_StoreIdAndIsHiddenFalseAndDeletedAtIsNull(storeId, Pageable.unpaged()))
                    .willReturn(page);

            Page<Product> result = productRepository.findByStore_StoreIdAndIsHiddenFalseAndDeletedAtIsNull(storeId, Pageable.unpaged());

            assertThat(result.getContent()).hasSize(2)
                    .extracting(Product::getProductName)
                    .containsExactlyInAnyOrder("상품1", "상품2");

            then(productJPARepository).should(times(1))
                    .findByStore_StoreIdAndIsHiddenFalseAndDeletedAtIsNull(storeId, Pageable.unpaged());
        }
    }

    // --------------------------------------------------------------------
    @Nested
    @DisplayName("findById() - 상품 ID 조회")
    class FindByIdTest {
        @Test
        @DisplayName("상품 ID로 조회 성공")
        void findById() {
            Product product = mock(Product.class);
            given(product.getProductName()).willReturn("상품1");
            given(productJPARepository.findById(productId1)).willReturn(Optional.of(product));

            Optional<Product> found = productRepository.findById(productId1);

            assertThat(found).isPresent();
            assertThat(found.get().getProductName()).isEqualTo("상품1");
        }
    }

    // --------------------------------------------------------------------
    @Nested
    @DisplayName("findWithOptionGroupsAndItemsByProductId() - 옵션 그룹 포함 조회")
    class FindWithOptionGroupsTest {
        @Test
        @DisplayName("옵션 그룹과 아이템 포함 조회 성공")
        void findWithOptionGroups() {
            Product product = mock(Product.class);
            given(product.getProductId()).willReturn(productId1);
            given(productJPARepository.findWithOptionGroupsAndItemsByProductId(productId1))
                    .willReturn(Optional.of(product));

            Optional<Product> result = productRepository.findWithOptionGroupsAndItemsByProductId(productId1);

            assertThat(result).isPresent();
            assertThat(result.get().getProductId()).isEqualTo(productId1);
        }
    }
}
