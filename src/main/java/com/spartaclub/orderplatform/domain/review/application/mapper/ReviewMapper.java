package com.spartaclub.orderplatform.domain.review.application.mapper;

import com.spartaclub.orderplatform.domain.review.domain.model.Review;
import com.spartaclub.orderplatform.domain.review.presentation.dto.response.ReviewResponseDto;
import com.spartaclub.orderplatform.domain.review.presentation.dto.response.ReviewSearchResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/*
 * 리뷰 Entity ↔ Dto 맵핑
 *
 * @author 이준성
 * @date 2025-10-07(화)
 */
@Mapper(componentModel = "spring")
public interface ReviewMapper {

    /*
     * 외래키
     * 고객ID userId
     * 가게ID storeId
     * 주문ID orderId
     * 메뉴ID product_id
     */

    // Entity → ResponseDto 변환
    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "storeId", source = "store.storeId")
    @Mapping(target = "orderId", source = "order.orderId")
    @Mapping(target = "productId", source = "product.productId")
    ReviewResponseDto toReviewResponseDto(Review review);

    @Mapping(target = "rating", source = "rating")
    @Mapping(target = "contents", source = "contents")
    ReviewSearchResponseDto toReviewSearchResponseDto(Review review);

}
