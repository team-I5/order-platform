package com.spartaclub.orderplatform.domain.product.application.service;

import com.spartaclub.orderplatform.domain.product.application.mapper.ProductOptionItemMapper;
import com.spartaclub.orderplatform.domain.product.domain.entity.OptionGroupTag;
import com.spartaclub.orderplatform.domain.product.domain.entity.ProductOptionGroup;
import com.spartaclub.orderplatform.domain.product.domain.entity.ProductOptionItem;
import com.spartaclub.orderplatform.domain.product.domain.repository.ProductOptionGroupRepository;
import com.spartaclub.orderplatform.domain.product.domain.repository.ProductOptionItemRepository;
import com.spartaclub.orderplatform.domain.product.exception.ProductErrorCode;
import com.spartaclub.orderplatform.domain.product.presentation.dto.ProductOptionItemRequestDto;
import com.spartaclub.orderplatform.domain.product.presentation.dto.ProductOptionItemResponseDto;
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
@DisplayName("ProductOptionItemService 단위 테스트")
class ProductOptionItemServiceTest {

    @Mock
    private ProductOptionGroupRepository productOptionGroupRepository;

    @Mock
    private ProductOptionItemRepository productOptionItemRepository;

    @Mock
    private ProductOptionItemMapper productOptionItemMapper;

    @InjectMocks
    private ProductOptionItemService productOptionItemService;

    private UUID optionItemId;
    private ProductOptionGroup optionGroup;
    private ProductOptionItem optionItem;
    private ProductOptionItemRequestDto productOptionItemRequestDto;
    private ProductOptionItemResponseDto productOptionItemResponseDto;

    @BeforeEach
    void setUp() {
        optionItemId = UUID.randomUUID();

        optionGroup = ProductOptionGroup.create("토핑 선택", OptionGroupTag.OPTIONAL, 0L, 3L);

        productOptionItemRequestDto = ProductOptionItemRequestDto.builder()
                .productOptionGroupId(UUID.randomUUID())
                .optionName("치즈 추가")
                .additionalPrice(1000L)
                .build();

        optionItem = ProductOptionItem.create(optionGroup, "치즈 추가", 1000L);
        productOptionItemResponseDto = ProductOptionItemResponseDto.builder()
                .optionName("치즈 추가")
                .additionalPrice(1000L)
                .build();
    }

    @Nested
    @DisplayName("createProductOptionItem() - 상품 옵션 아이템 생성")
    class CreateProductOptionItemTest {

        @Test
        @DisplayName("옵션 그룹이 존재하면 아이템이 생성되어 DTO로 반환된다.")
        void success_createProductOptionItem() {
            // given
            given(productOptionGroupRepository.findById(productOptionItemRequestDto.getProductOptionGroupId()))
                    .willReturn(Optional.of(optionGroup));
            given(productOptionItemMapper.toResponseDto(any(ProductOptionItem.class)))
                    .willReturn(productOptionItemResponseDto);

            // when
            ProductOptionItemResponseDto result =
                    productOptionItemService.createProductOptionItem(productOptionItemRequestDto);

            // then
            then(productOptionGroupRepository).should(times(1))
                    .findById(productOptionItemRequestDto.getProductOptionGroupId());
            assertThat(result)
                    .isNotNull()
                    .extracting("optionName", "additionalPrice")
                    .containsExactly("치즈 추가", 1000L);
        }

        @Test
        @DisplayName("존재하지 않는 옵션 그룹이면 BusinessException이 발생한다.")
        void fail_createProductOptionItem_groupNotFound() {
            // given
            given(productOptionGroupRepository.findById(any(UUID.class)))
                    .willReturn(Optional.empty());

            // when
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> productOptionItemService.createProductOptionItem(productOptionItemRequestDto));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ProductErrorCode.PRODUCT_OPTION_GROUP_NOT_EXIST);
        }
    }

    @Nested
    @DisplayName("updateProductOptionItem() - 상품 옵션 아이템 수정")
    class UpdateProductOptionItemTest {

        @Test
        @DisplayName("존재하는 아이템 수정 시 변경된 DTO가 반환된다.")
        void success_updateProductOptionItem() {
            // given
            given(productOptionItemRepository.findById(optionItemId)).willReturn(Optional.of(optionItem));
            given(productOptionItemMapper.toResponseDto(any(ProductOptionItem.class)))
                    .willReturn(productOptionItemResponseDto);

            ProductOptionItemRequestDto updateDto = ProductOptionItemRequestDto.builder()
                    .productOptionGroupId(optionGroup.getProductOptionGroupId())
                    .optionName("베이컨 추가")
                    .additionalPrice(1500L)
                    .build();

            // when
            ProductOptionItemResponseDto result =
                    productOptionItemService.updateProductOptionItem(optionItemId, updateDto);

            // then
            then(productOptionItemRepository).should(times(1)).findById(optionItemId);
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("존재하지 않는 아이템 수정 시 BusinessException이 발생한다.")
        void fail_updateProductOptionItem_notFound() {
            // given
            given(productOptionItemRepository.findById(optionItemId)).willReturn(Optional.empty());

            // when
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> productOptionItemService.updateProductOptionItem(optionItemId, productOptionItemRequestDto));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ProductErrorCode.PRODUCT_OPTION_ITEM_N0T_EXIST);
        }
    }

    @Nested
    @DisplayName("deleteProductOptionItem() - 상품 옵션 아이템 삭제")
    class DeleteProductOptionItemTest {

        @Test
        @DisplayName("존재하는 아이템 삭제 시 정상적으로 soft delete된다.")
        void success_deleteProductOptionItem() {
            // given
            given(productOptionItemRepository.findById(optionItemId)).willReturn(Optional.of(optionItem));

            // when
            productOptionItemService.deleteProductOptionItem(1L, optionItemId);

            // then
            assertThat(optionItem).extracting("deletedId").isEqualTo(1L);
            assertThat(optionItem).extracting("deletedAt").isNotNull();
        }

        @Test
        @DisplayName("존재하지 않는 아이템 삭제 시 BusinessException이 발생한다.")
        void fail_deleteProductOptionItem_notFound() {
            // given
            given(productOptionItemRepository.findById(optionItemId)).willReturn(Optional.empty());

            // when
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> productOptionItemService.deleteProductOptionItem(1L, optionItemId));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ProductErrorCode.PRODUCT_OPTION_ITEM_N0T_EXIST);
        }
    }
}
