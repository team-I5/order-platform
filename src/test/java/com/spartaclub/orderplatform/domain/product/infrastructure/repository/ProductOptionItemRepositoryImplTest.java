package com.spartaclub.orderplatform.domain.product.infrastructure.repository;

import com.spartaclub.orderplatform.domain.product.domain.entity.ProductOptionItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductOptionItemRepositoryImpl 단위 테스트 (Mock)")
class ProductOptionItemRepositoryImplTest {

    @Mock
    private ProductOptionItemJPARepository productOptionItemJPARepository;

    @InjectMocks
    private ProductOptionItemRepositoryImpl productOptionItemRepository;

    @Nested
    @DisplayName("findById() - 옵션 아이템 조회")
    class FindByIdTest {
        @Test
        @DisplayName("옵션 아이템 ID로 조회 성공")
        void findById_success() {
            // given
            UUID itemId = UUID.randomUUID();
            ProductOptionItem mockItem = mock(ProductOptionItem.class);
            given(productOptionItemJPARepository.findById(itemId)).willReturn(Optional.of(mockItem));

            // when
            Optional<ProductOptionItem> found = productOptionItemRepository.findById(itemId);

            // then
            assertThat(found).isPresent();
            then(productOptionItemJPARepository).should(times(1)).findById(itemId);
        }

        @Test
        @DisplayName("존재하지 않는 옵션 아이템 ID면 Optional.empty()를 반환한다")
        void findById_notFound() {
            // given
            UUID itemId = UUID.randomUUID();
            given(productOptionItemJPARepository.findById(itemId)).willReturn(Optional.empty());

            // when
            Optional<ProductOptionItem> found = productOptionItemRepository.findById(itemId);

            // then
            assertThat(found).isEmpty();
            then(productOptionItemJPARepository).should(times(1)).findById(itemId);
        }
    }
}
