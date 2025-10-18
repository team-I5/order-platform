package com.spartaclub.orderplatform.domain.review.presentation.dto.request;

/*
 * 리뷰 등록 요청 DTO 클래스
 *
 * @author 이준성
 * @date 2025-10-02(목)
 */

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "리뷰 요청 정보 ")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCreateRequestDto {

    // 외래키 ID
    @Schema(description = "음식점ID", example = "0849b6dc-a859-4d71-9a97-86e23be0ef18")
    @NotNull(message = "음식점 리뷰 생성을 위한 음식점 ID 필요합니다.")
    private UUID storeId;
    // 주문 Id for 중복체크
    @Schema(description = "주문ID", example = "0849b6dc-a859-4d71-9a97-86e23be0ef18")
    @NotNull(message = "리뷰 중복 체크를 위한 주문 ID 필요합니다.")
    private UUID orderId;
    @Schema(description = "상품ID", example = "0849b6dc-a859-4d71-9a97-86e23be0ef18")
    @NotNull(message = "음식 리뷰 생성을 위한 상품 ID 필요합니다.")
    private UUID productId;
    // 별점
    @Schema(description = "리뷰 별점(1~5점)", example = "3")
    @NotNull(message = "서비스를 평가해주세요.")
    private Integer rating;
    // 리뷰 내용
    @Schema(description = "리뷰 내용", example = "친절해서 좋았습니다.")
    @Size(max = 1000, message = "리뷰는 1000자 이내로 작성해주세요.")
    private String contents;


    public static ReviewCreateRequestDto of(UUID storeId, UUID orderId, UUID productId,
        Integer rating, String contents) {
        return new ReviewCreateRequestDto(storeId, orderId, productId, rating, contents);
    }
}
