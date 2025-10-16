package com.spartaclub.orderplatform.domain.product.application.mapper;

import com.spartaclub.orderplatform.domain.product.domain.entity.Product;
import com.spartaclub.orderplatform.domain.product.domain.entity.ProductOptionGroup;
import com.spartaclub.orderplatform.domain.product.domain.entity.ProductOptionMap;
import com.spartaclub.orderplatform.domain.product.presentation.dto.*;
import com.spartaclub.orderplatform.domain.review.entity.Review;
import com.spartaclub.orderplatform.domain.store.domain.model.Store;
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

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.nickname", target = "nickName")
    ProductReviewResponseDto toReviewDto(Review review);


    ProductStoreSearchResponseDto toProductStoreSearchResponseDto(Store store);
}