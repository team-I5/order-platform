package com.spartaclub.orderplatform.domain.product.application.service;

import com.spartaclub.orderplatform.domain.product.application.mapper.ProductOptionGroupMapper;
import com.spartaclub.orderplatform.domain.product.domain.entity.ProductOptionGroup;
import com.spartaclub.orderplatform.domain.product.domain.repository.ProductOptionGroupRepository;
import com.spartaclub.orderplatform.domain.product.exception.ProductErrorCode;
import com.spartaclub.orderplatform.domain.product.infrastructure.repository.ProductOptionGroupJPARepository;
import com.spartaclub.orderplatform.domain.product.presentation.dto.ProductOptionGroupRequestDto;
import com.spartaclub.orderplatform.domain.product.presentation.dto.ProductOptionGroupResponseDto;
import com.spartaclub.orderplatform.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductOptionGroupService {

    private final ProductOptionGroupRepository productOptionGroupRepository;
    private final ProductOptionGroupMapper productOptionGroupMapper;

    @Transactional
    public ProductOptionGroupResponseDto createProductOptionGroup(ProductOptionGroupRequestDto productOptionGroupRequestDto) {
        // 1. group 생성
        ProductOptionGroup group = ProductOptionGroup.create(
                productOptionGroupRequestDto.getOptionGroupName(),
                productOptionGroupRequestDto.getTag(),
                productOptionGroupRequestDto.getMinSelect(),
                productOptionGroupRequestDto.getMaxSelect()
        );

        // 2. DB 저장
        ProductOptionGroup saved = productOptionGroupRepository.save(group);

        // 3. -> Dto 후 반환
        return productOptionGroupMapper.toResponseDto(saved);
    }

    @Transactional
    public ProductOptionGroupResponseDto updateProductOptionGroup(UUID productOptionGroupId, ProductOptionGroupRequestDto productOptionGroupRequestDto) {
        // 1️. 수정할 옵션 그룹 조회
        ProductOptionGroup productOptionGroup = getProductOptionGroup(productOptionGroupId);

        // 2️. 수정 대상 필드 변경
        productOptionGroup.updateOptionGroupInfo(productOptionGroupRequestDto);

        // 3️. 변경 감지는 @Transactional 덕분에 자동 반영됨 (save 필요 없음)

        // 4. Entity -> Dto 변환 후 반환
        return productOptionGroupMapper.toResponseDto(productOptionGroup);
    }


    @Transactional
    public void deleteProductOptionGroup(Long userId, UUID productOptionGroupId) {
        ProductOptionGroup group = getProductOptionGroup(productOptionGroupId);

        group.deleteOptionGroup(userId);
    }

    // 상품 옵션 그룹 조회 공통 메소드
    private ProductOptionGroup getProductOptionGroup(UUID productOptionGroupId) {
        return productOptionGroupRepository.findById(productOptionGroupId)
                .orElseThrow(() -> new BusinessException(ProductErrorCode.PRODUCT_OPTION_GROUP_NOT_EXIST));
    }

}
