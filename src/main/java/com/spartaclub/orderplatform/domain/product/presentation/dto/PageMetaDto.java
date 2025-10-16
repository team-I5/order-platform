package com.spartaclub.orderplatform.domain.product.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "페이지 메타 정보 DTO")
public class PageMetaDto {

    @Schema(description = "현재 페이지 번호 (0부터 시작)")
    private int pageNumber;

    @Schema(description = "페이지 크기")
    private int pageSize;

    @Schema(description = "전체 아이템 수")
    private long totalElements;

    @Schema(description = "전체 페이지 수")
    private int totalPages;

    @Schema(description = "첫 번째 페이지 여부")
    private boolean isFirst;

    @Schema(description = "마지막 페이지 여부")
    private boolean isLast;
}
