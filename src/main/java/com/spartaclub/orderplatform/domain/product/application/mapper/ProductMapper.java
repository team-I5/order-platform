package com.spartaclub.orderplatform.domain.product.application.mapper;

import com.spartaclub.orderplatform.domain.product.domain.entity.Product;
import com.spartaclub.orderplatform.domain.product.domain.entity.ProductOptionGroup;
import com.spartaclub.orderplatform.domain.product.domain.entity.ProductOptionMap;
import com.spartaclub.orderplatform.domain.product.presentation.dto.*;
import com.spartaclub.orderplatform.domain.review.entity.Review;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "productOptionGroupMaps", ignore = true)
    @Mapping(target = "productId", ignore = true) // DB에서 생성
    @Mapping(target = "createdId", ignore = true) // Audit 자동 세팅
    @Mapping(target = "modifiedId", ignore = true)
    @Mapping(target = "deletedId", ignore = true)
    Product toEntity(ProductCreateRequestDto dto);

    // Entity → ResponseDto 변환
    // store.storeId → dto.storeId 로 매핑
    @Mapping(target = "storeId", source = "store.storeId")
    ProductResponseDto toDto(Product product);

    @Mapping(source = "productOptionGroupMaps", target = "productOptionGroups", qualifiedByName = "productOptionMapListToGroupDtoList")
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
    }

    // MapStruct가 직접 생성하지 않고 create()를 사용하도록 지정
    @ObjectFactory
    default Product createProduct(ProductCreateRequestDto dto) {
        return Product.create(
                dto.getProductName(),
                dto.getPrice(),
                dto.getProductDescription()
        );
    }

    @Named("productOptionMapListToGroupDtoList")
    default List<ProductOptionGroupResponseDto> productOptionMapListToGroupDtoList(
            List<ProductOptionMap> maps) {
        if (maps == null) return Collections.emptyList();
        return maps.stream()
                .map(ProductOptionMap::getProductOptionGroup)
                .map(this::toGroupDto)
                .collect(Collectors.toList());
    }

    ProductOptionGroupResponseDto toGroupDto(ProductOptionGroup group);

    ProductReviewResponseDto toReviewDto(Review review);
}