package com.spartaclub.orderplatform.domain.product.application.service;

import com.spartaclub.orderplatform.domain.product.application.mapper.ProductOptionItemMapper;
import com.spartaclub.orderplatform.domain.product.domain.entity.ProductOptionGroup;
import com.spartaclub.orderplatform.domain.product.domain.repository.ProductOptionGroupRepository;
import com.spartaclub.orderplatform.domain.product.domain.repository.ProductOptionItemRepository;
import com.spartaclub.orderplatform.domain.product.exception.ProductErrorCode;
import com.spartaclub.orderplatform.domain.product.infrastructure.repository.ProductOptionGroupJPARepository;
import com.spartaclub.orderplatform.domain.product.presentation.dto.ProductOptionItemRequestDto;
import com.spartaclub.orderplatform.domain.product.presentation.dto.ProductOptionItemResponseDto;
import com.spartaclub.orderplatform.domain.product.domain.entity.ProductOptionItem;
import com.spartaclub.orderplatform.domain.product.infrastructure.repository.ProductOptionItemJPARepository;
import com.spartaclub.orderplatform.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductOptionItemService {

    private final ProductOptionGroupRepository productOptionGroupRepository;
    private final ProductOptionItemRepository productOptionItemRepository;
    private final ProductOptionItemMapper productOptionItemMapper;

    @Transactional
    public ProductOptionItemResponseDto createProductOptionItem(ProductOptionItemRequestDto productOptionItemRequestDto) {
        // 1. 옵션 그룹 조회
        ProductOptionGroup productOptionGroup = productOptionGroupRepository.findById(productOptionItemRequestDto.getProductOptionGroupId())
                .orElseThrow(() -> new BusinessException(ProductErrorCode.PRODUCT_OPTION_GROUP_NOT_EXIST));

        // 2. 아이템 엔티티 생성
        ProductOptionItem item = ProductOptionItem.create(
                productOptionGroup,
                productOptionItemRequestDto.getOptionName(),
                productOptionItemRequestDto.getAdditionalPrice());

        // 3. 그룹의 아이템 리스트에 추가
        productOptionGroup.addItem(item);

        // 4. 옵션 group, item은 persist 상태에서 자동 저장

        return productOptionItemMapper.toResponseDto(item);
    }

    @Transactional
    public ProductOptionItemResponseDto updateProductOptionItem(UUID productOptionItemId, ProductOptionItemRequestDto productOptionItemRequestDto) {
        // 1. 상품 옵션 조회
        ProductOptionItem optionItem = getOptionItem(productOptionItemId);

        // 2. 상품 옵션 수정
        optionItem.updateOptionItem(productOptionItemRequestDto);

        // 3. DB 변경 자동 감지

        // 4. Dto 변환 후 반환
        return productOptionItemMapper.toResponseDto(optionItem);
    }

    @Transactional
    public void deleteProductOptionItem(Long userId, UUID productOptionItemId) {
        // 1. 상품 옵션 조회
        ProductOptionItem optionItem = getOptionItem(productOptionItemId);

        // 2. 삭제
        optionItem.deleteItem(userId);
    }

    // 상품 옵션 조회 공통 메소드
    private ProductOptionItem getOptionItem(UUID productOptionItemId) {
        return productOptionItemRepository.findById(productOptionItemId)
                .orElseThrow(() -> new BusinessException(ProductErrorCode.PRODUCT_OPTION_ITEM_N0T_EXIST));
    }

}
