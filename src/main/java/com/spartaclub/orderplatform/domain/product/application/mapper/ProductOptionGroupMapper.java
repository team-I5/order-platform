package com.spartaclub.orderplatform.domain.product.application.mapper;

import com.spartaclub.orderplatform.domain.product.domain.entity.ProductOptionGroup;
import com.spartaclub.orderplatform.domain.product.presentation.dto.ProductOptionGroupRequestDto;
import com.spartaclub.orderplatform.domain.product.presentation.dto.ProductOptionGroupResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductOptionGroupMapper {

    // DTO → Entity
    @Mapping(target = "productOptionGroupId", ignore = true)
    @Mapping(target = "createdId", ignore = true)
    @Mapping(target = "modifiedId", ignore = true)
    @Mapping(target = "deletedId", ignore = true)
    ProductOptionGroup toEntity(ProductOptionGroupRequestDto dto);

    // Entity → DTO
    ProductOptionGroupResponseDto toResponseDto(ProductOptionGroup entity);
}
