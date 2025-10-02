package com.spartaclub.orderplatform.domain.product.mapper;

import com.spartaclub.orderplatform.domain.product.dto.ProductRequestDto;
import com.spartaclub.orderplatform.domain.product.dto.ProductResponseDto;
import com.spartaclub.orderplatform.domain.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
/**
 * 상품 Entity <-> Dto 매핑
 *
 * @author 류형선
 * @date 2025-10-02(목)
 */
@Mapper(componentModel = "spring")
public interface ProductMapper {

    // RequestDto → Entity 변환
    // store는 Service에서 주입해야 하므로 무시(ignore)
//    @Mapping(target = "store", ignore = true)
    @Mapping(target = "productId", ignore = true) // DB에서 생성
    @Mapping(target = "createdId", ignore = true) // Audit 자동 세팅
    @Mapping(target = "modifiedId", ignore = true)
    @Mapping(target = "deletedId", ignore = true)
    Product toEntity(ProductRequestDto dto);

    // Entity → ResponseDto 변환
    // store.storeId → dto.storeId 로 매핑
//    @Mapping(target = "storeId", source = "store.storeId")
    ProductResponseDto toDto(Product product);
}