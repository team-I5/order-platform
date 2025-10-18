package com.spartaclub.orderplatform.domain.product.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "페이지 응답 DTO")
public class PageResponseDto<T> {

    @Schema(description = "페이지 내 데이터 목록")
    private List<T> content; // 실제 데이터

    @Schema(description = "페이지 메타 정보")
    private PageMetaDto meta;   // 페이지 메타 데이터
}
