package com.spartaclub.orderplatform.domain.product.infrastructure.repository;

import com.spartaclub.orderplatform.domain.product.domain.entity.ProductOptionGroup;
import com.spartaclub.orderplatform.domain.user.domain.entity.Address;
import org.junit.jupiter.api.BeforeEach;
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

    private UUID groupId;
    private ProductOptionGroup productOptionGroup;

    @BeforeEach
    void setUp() {
        groupId = UUID.randomUUID();
        productOptionGroup = mock(ProductOptionGroup.class);
    }


    @Nested
    @DisplayName("findById() - 옵션 그룹 조회")
    class FindByIdTest {
        @Test
        @DisplayName("옵션 그룹 ID로 조회 성공")
        void findById() {
            // given
            given(productOptionGroupJPARepository.findById(groupId)).willReturn(Optional.of(productOptionGroup));

            // when
            Optional<ProductOptionGroup> result = productOptionGroupRepository.findById(groupId);

            // then
            assertThat(result).isPresent();
            assertThat(result).contains(productOptionGroup);
        }
    }

    @Nested
    @DisplayName("save() - 옵션 그룹 저장")
    class SaveTest {
        @Test
        @DisplayName("옵션 그룹이 정상적으로 저장된다")
        void saveOptionGroup() {
            // given
            given(productOptionGroupJPARepository.save(productOptionGroup)).willReturn(productOptionGroup);

            // when
            ProductOptionGroup saved = productOptionGroupRepository.save(productOptionGroup);

            // then
            assertThat(saved).isNotNull();
            assertThat(saved).isEqualTo(productOptionGroup);
            then(productOptionGroupJPARepository).should(times(1)).save(productOptionGroup);
        }
    }
}
