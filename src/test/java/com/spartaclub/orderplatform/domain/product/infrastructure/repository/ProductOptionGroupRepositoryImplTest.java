package com.spartaclub.orderplatform.domain.product.infrastructure.repository;

import com.spartaclub.orderplatform.domain.product.domain.entity.ProductOptionGroup;
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
@DisplayName("ProductOptionGroupRepositoryImpl 단위 테스트 (Mock)")
class ProductOptionGroupRepositoryImplTest {

    @Mock
    private ProductOptionGroupJPARepository productOptionGroupJPARepository;

    @InjectMocks
    private ProductOptionGroupRepositoryImpl productOptionGroupRepository;

    @Nested
    @DisplayName("findById() - 옵션 그룹 조회")
    class FindByIdTest {
        @Test
        @DisplayName("옵션 그룹 ID로 조회 성공")
        void findById() {
            // given
            UUID groupId = UUID.randomUUID();
            ProductOptionGroup group = mock(ProductOptionGroup.class);
            given(productOptionGroupJPARepository.findById(groupId)).willReturn(Optional.of(group));

            // when
            Optional<ProductOptionGroup> found = productOptionGroupRepository.findById(groupId);

            // then
            assertThat(found).isPresent();
            then(productOptionGroupJPARepository).should(times(1)).findById(groupId);
        }
    }

    @Nested
    @DisplayName("save() - 옵션 그룹 저장")
    class SaveTest {
        @Test
        @DisplayName("옵션 그룹이 정상적으로 저장된다")
        void saveOptionGroup() {
            // given
            ProductOptionGroup group = mock(ProductOptionGroup.class);
            given(productOptionGroupJPARepository.save(group)).willReturn(group);

            // when
            ProductOptionGroup saved = productOptionGroupRepository.save(group);

            // then
            assertThat(saved).isNotNull();
            then(productOptionGroupJPARepository).should(times(1)).save(group);
        }
    }
}
