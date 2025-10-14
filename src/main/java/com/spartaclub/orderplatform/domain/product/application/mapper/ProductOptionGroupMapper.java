package com.spartaclub.orderplatform.domain.product.application.mapper;

import com.spartaclub.orderplatform.domain.product.domain.entity.ProductOptionGroup;
import com.spartaclub.orderplatform.domain.product.presentation.dto.ProductOptionGroupRequestDto;
import com.spartaclub.orderplatform.domain.product.presentation.dto.ProductOptionGroupResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ObjectFactory;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductOptionGroupMapper {

    // DTO → Entity
    @Mapping(target = "productOptionGroupId", ignore = true)
    @Mapping(target = "createdId", ignore = true)
    @Mapping(target = "modifiedId", ignore = true)
    @Mapping(target = "deletedId", ignore = true)
    @Mapping(target = "productOptionGroupMaps", ignore = true)
    @Mapping(target = "optionItems", ignore = true)
    ProductOptionGroup toEntity(ProductOptionGroupRequestDto dto);

    // Entity → DTO
    ProductOptionGroupResponseDto toResponseDto(ProductOptionGroup entity);

    @ObjectFactory
    default ProductOptionGroup createProduct(ProductOptionGroupRequestDto dto) {
        return ProductOptionGroup.create(
                dto.getOptionGroupName(),
                dto.getTag(),
                dto.getMinSelect(),
                dto.getMaxSelect()
        );
    }
}
