package com.spartaclub.orderplatform.domain.product.presentation.dto;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PageMetaDto {
    private int pageNumber;      // 현재 페이지 (0-based)
    private int pageSize;        // 페이지 사이즈
    private long totalElements;  // 전체 아이템 수
    private int totalPages;      // 전체 페이지 수
    private boolean isFirst;
    private boolean isLast;
}