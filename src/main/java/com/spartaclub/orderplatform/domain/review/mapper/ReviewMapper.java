package com.spartaclub.orderplatform.domain.review.mapper;

import com.spartaclub.orderplatform.domain.review.dto.ReviewCreateRequestDto;
import com.spartaclub.orderplatform.domain.review.dto.ReviewResponseDto;
import com.spartaclub.orderplatform.domain.review.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

/*
 * 리뷰 Entity ↔ Dto 맵핑
 *
 * @author 이준성
 * @date 2025-10-07(화)
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ReviewMapper {
    // RequestDto → Entity 변환
    /* 외래키
     * 고객ID userId
     * 가게ID storeId
     * 주문ID orderId
     * 메뉴ID product_id
     */
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "store", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "product", ignore = true)
    // 생성자, 수정자, 삭제자
    @Mapping(target = "createdId", ignore = true)
    @Mapping(target = "modifiedId", ignore = true)
    @Mapping(target = "deletedId", ignore = true)
    Review toReviewEntity(ReviewCreateRequestDto reviewCreateRequestDto);

    // Entity → ResponseDto 변환
    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "storeId", source = "store.storeId")
    @Mapping(target = "orderId", source = "order.orderId")
    @Mapping(target = "productId", source = "product.productId")
    ReviewResponseDto toReviewDto(Review review);

}
