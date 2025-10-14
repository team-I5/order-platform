package com.spartaclub.orderplatform.domain.product.application.mapper;

import com.spartaclub.orderplatform.domain.product.domain.entity.ProductOptionGroup;
import com.spartaclub.orderplatform.domain.product.domain.entity.ProductOptionItem;
import com.spartaclub.orderplatform.domain.product.presentation.dto.ProductOptionItemRequestDto;
import com.spartaclub.orderplatform.domain.product.presentation.dto.ProductOptionItemResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ObjectFactory;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductOptionItemMapper {

    // DTO → Entity
    @Mapping(target = "productOptionItemId", ignore = true)
    @Mapping(target = "createdId", ignore = true)
    @Mapping(target = "modifiedId", ignore = true)
    @Mapping(target = "deletedId", ignore = true)
    ProductOptionItem toEntity(ProductOptionGroup productOptionGroup, ProductOptionItemRequestDto dto);

    // Entity → DTO
    ProductOptionItemResponseDto toResponseDto(ProductOptionItem entity);

    @ObjectFactory
    default ProductOptionItem createProduct(ProductOptionGroup productOptionGroup, ProductOptionItemRequestDto dto) {
        return ProductOptionItem.create(
                productOptionGroup,
                dto.getOptionName(),
                dto.getAdditionalPrice()
        );
    }
}
