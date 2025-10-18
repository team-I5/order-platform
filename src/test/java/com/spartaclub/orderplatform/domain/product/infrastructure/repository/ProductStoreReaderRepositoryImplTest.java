package com.spartaclub.orderplatform.domain.product.infrastructure.repository;

import com.spartaclub.orderplatform.domain.store.domain.model.Store;
import com.spartaclub.orderplatform.domain.store.infrastructure.repository.StoreJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductStoreReaderRepositoryImpl 단위 테스트 (Mock 기반)")
class ProductStoreReaderRepositoryImplTest {

    @Mock
    private StoreJpaRepository storeJpaRepository;

    @InjectMocks
    private ProductStoreReaderRepositoryImpl productStoreReaderRepository;

    private Store store;
    private UUID storeId;

    @BeforeEach
    void setUp() {
        storeId = UUID.randomUUID();
        store = mock(Store.class);
    }

    @Nested
    @DisplayName("findDistinctByProductNameContainingIgnoreCase() 테스트")
    class FindDistinctByProductNameContainingIgnoreCaseTest {

        @Test
        @DisplayName("상품명 키워드로 상점 조회 성공")
        void findDistinctByProductNameContainingIgnoreCase_success() {
            // given
            String keyword = "커피";
            String roadName = "강남";
            Pageable pageable = PageRequest.of(0, 10);

            Page<Store> mockPage = new PageImpl<>(List.of(store));

            given(storeJpaRepository.findDistinctByProductNameContainingIgnoreCase(keyword, roadName, pageable))
                    .willReturn(mockPage);

            // when
            Page<Store> result = productStoreReaderRepository.findDistinctByProductNameContainingIgnoreCase(keyword, roadName, pageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0)).isEqualTo(store);
        }
    }

    @Nested
    @DisplayName("findById() 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("storeId로 상점 조회 성공")
        void findById_success() {
            // given
            given(storeJpaRepository.findById(storeId)).willReturn(Optional.of(store));

            // when
            Optional<Store> result = productStoreReaderRepository.findById(storeId);

            // then
            assertThat(result).isPresent();
            assertThat(result).contains(store);
            then(storeJpaRepository).should(times(1)).findById(storeId);
        }

        @Test
        @DisplayName("존재하지 않는 storeId일 경우 Optional.empty() 반환")
        void findById_notFound() {
            // given
            given(storeJpaRepository.findById(storeId)).willReturn(Optional.empty());

            // when
            Optional<Store> result = productStoreReaderRepository.findById(storeId);

            // then
            assertThat(result).isEmpty();
            then(storeJpaRepository).should(times(1)).findById(storeId);
        }
    }
}
