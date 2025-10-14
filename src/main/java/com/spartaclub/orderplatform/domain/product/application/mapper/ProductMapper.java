package com.spartaclub.orderplatform.domain.product.application.mapper;

import com.spartaclub.orderplatform.domain.product.presentation.dto.PageMetaDto;
import com.spartaclub.orderplatform.domain.product.presentation.dto.ProductCreateRequestDto;
import com.spartaclub.orderplatform.domain.product.presentation.dto.ProductDetailResponseDto;
import com.spartaclub.orderplatform.domain.product.presentation.dto.ProductResponseDto;
import com.spartaclub.orderplatform.domain.product.domain.entity.Product;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

/**
 * 상품 Entity <-> Dto 매핑
 *
 * @author 류형선
 * @date 2025-10-02(목)
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {

    // RequestDto → Entity 변환
    // store는 Service에서 주입해야 하므로 무시(ignore)
    @Mapping(target = "store", ignore = true)
    @Mapping(target = "productId", ignore = true) // DB에서 생성
    @Mapping(target = "createdId", ignore = true) // Audit 자동 세팅
    @Mapping(target = "modifiedId", ignore = true)
    @Mapping(target = "deletedId", ignore = true)
    Product toEntity(ProductCreateRequestDto dto);

    // Entity → ResponseDto 변환
    // store.storeId → dto.storeId 로 매핑
    @Mapping(target = "storeId", source = "store.storeId")
    ProductResponseDto toDto(Product product);

    ProductDetailResponseDto toResponseDto(Product product);

    // Page Entity -> dto 변환
    // Page 객체는 자옫 변환이 불가능해서 자동 구현 x
    default PageMetaDto toPageDto(Page<?> productPage) {
        if (productPage == null) {
            return null;
        }

        return PageMetaDto.builder()
                .pageNumber(productPage.getNumber())
                .pageSize(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .isFirst(productPage.isFirst())
                .isLast(productPage.isLast())
                .build();
    }}