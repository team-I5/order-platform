package com.spartaclub.orderplatform.domain.product.application.service;

import com.spartaclub.orderplatform.domain.product.application.mapper.ProductOptionGroupMapper;
import com.spartaclub.orderplatform.domain.product.domain.entity.OptionGroupTag;
import com.spartaclub.orderplatform.domain.product.domain.entity.ProductOptionGroup;
import com.spartaclub.orderplatform.domain.product.domain.repository.ProductOptionGroupRepository;
import com.spartaclub.orderplatform.domain.product.exception.ProductErrorCode;
import com.spartaclub.orderplatform.domain.product.presentation.dto.ProductOptionGroupRequestDto;
import com.spartaclub.orderplatform.domain.product.presentation.dto.ProductOptionGroupResponseDto;
import com.spartaclub.orderplatform.global.exception.BusinessException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductOptionGroupService 단위 테스트")
class ProductOptionGroupServiceTest {

    @Mock
    private ProductOptionGroupRepository productOptionGroupRepository;

    @Mock
    private ProductOptionGroupMapper productOptionGroupMapper;

    @InjectMocks
    private ProductOptionGroupService productOptionGroupService;

    private UUID optionGroupId;
    private ProductOptionGroup productOptionGroup;
    private ProductOptionGroupRequestDto productOptionGroupRequestDto;
    private ProductOptionGroupResponseDto productOptionGroupResponseDto;

    @BeforeEach
    void setUp() {
        optionGroupId = UUID.randomUUID();



        productOptionGroupRequestDto = ProductOptionGroupRequestDto.builder()
                .optionGroupName("토핑 선택")
                .tag(OptionGroupTag.REQUIRED)
                .minSelect(0L)
                .maxSelect(3L)
                .build();
        productOptionGroup = ProductOptionGroup.create(
                productOptionGroupRequestDto.getOptionGroupName(),
                productOptionGroupRequestDto.getTag(),
                productOptionGroupRequestDto.getMinSelect(),
                productOptionGroupRequestDto.getMaxSelect()
        );
        productOptionGroupResponseDto = ProductOptionGroupResponseDto.builder()
                .productOptionGroupId(optionGroupId)
                .optionGroupName("토핑 선택")
                .tag(OptionGroupTag.REQUIRED)
                .minSelect(0L)
                .maxSelect(3L)
                .build();

    }

    @Nested
    @DisplayName("createProductOptionGroup() - 상품 옵션 그룹 생성")
    class CreateProductOptionGroupTest {

        @Test
        @DisplayName("옵션 그룹 생성에 성공하면, DTO로 변환되어 반환된다.")
        void success_createProductOptionGroup() {
            // given
            given(productOptionGroupRepository.save(any(ProductOptionGroup.class))).willReturn(productOptionGroup);
            given(productOptionGroupMapper.toResponseDto(any(ProductOptionGroup.class))).willReturn(productOptionGroupResponseDto);

            // when
            ProductOptionGroupResponseDto result = productOptionGroupService.createProductOptionGroup(productOptionGroupRequestDto);

            // then
            then(productOptionGroupRepository).should(times(1)).save(any(ProductOptionGroup.class));
            assertThat(result)
                    .isNotNull()
                    .extracting("optionGroupName")
                    .isEqualTo("토핑 선택");
        }
    }

    @Nested
    @DisplayName("updateProductOptionGroup() - 상품 옵션 그룹 수정")
    class UpdateProductOptionGroupTest {

        @Test
        @DisplayName("존재하는 옵션 그룹의 정보를 수정하면, 수정된 DTO가 반환된다.")
        void success_updateProductOptionGroup() {
            // given
            productOptionGroupRequestDto = ProductOptionGroupRequestDto.builder()
                    .optionGroupName("맵기 선택")
                    .tag(OptionGroupTag.OPTIONAL)
                    .minSelect(1L)
                    .maxSelect(1L)
                    .build();
            productOptionGroupResponseDto = ProductOptionGroupResponseDto.builder()
                    .productOptionGroupId(optionGroupId)
                    .optionGroupName("맵기 선택")
                    .tag(OptionGroupTag.OPTIONAL)
                    .minSelect(1L)
                    .maxSelect(1L)
                    .build();

            given(productOptionGroupRepository.findById(optionGroupId)).willReturn(Optional.of(productOptionGroup));
            given(productOptionGroupMapper.toResponseDto(any(ProductOptionGroup.class))).willReturn(productOptionGroupResponseDto);

            // when
            ProductOptionGroupResponseDto result =
                    productOptionGroupService.updateProductOptionGroup(optionGroupId, productOptionGroupRequestDto);

            // then
            assertThat(result)
                    .isNotNull()
                    .extracting("optionGroupName", "tag", "minSelect", "maxSelect")
                    .containsExactly("맵기 선택", OptionGroupTag.OPTIONAL, 1L, 1L);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 수정 요청 시 BusinessException이 발생한다.")
        void fail_updateProductOptionGroup_notFound() {
            // given
            given(productOptionGroupRepository.findById(optionGroupId)).willReturn(Optional.empty());

            // when
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> productOptionGroupService.updateProductOptionGroup(optionGroupId, productOptionGroupRequestDto));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ProductErrorCode.PRODUCT_OPTION_GROUP_NOT_EXIST);
        }
    }

    @Nested
    @DisplayName("deleteProductOptionGroup() - 상품 옵션 그룹 삭제")
    class DeleteProductOptionGroupTest {

        @Test
        @DisplayName("존재하는 옵션 그룹 삭제 시 정상적으로 삭제된다.")
        void success_deleteProductOptionGroup() {
            // given
            given(productOptionGroupRepository.findById(optionGroupId)).willReturn(Optional.of(productOptionGroup));

            // when
            productOptionGroupService.deleteProductOptionGroup(1L, optionGroupId);

            // then
            assertThat(productOptionGroup).extracting("deletedId").isEqualTo(1L);
            assertThat(productOptionGroup).extracting("deletedAt").isNotNull();
        }

        @Test
        @DisplayName("존재하지 않는 옵션 그룹 삭제 시 BusinessException이 발생한다.")
        void fail_deleteProductOptionGroup_notFound() {
            // given
            given(productOptionGroupRepository.findById(optionGroupId)).willReturn(Optional.empty());

            // when
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> productOptionGroupService.deleteProductOptionGroup(1L, optionGroupId));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ProductErrorCode.PRODUCT_OPTION_GROUP_NOT_EXIST);
        }
    }
}
