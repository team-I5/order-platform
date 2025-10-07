package com.spartaclub.orderplatform.domain.product.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResponseDto<T> {
    private List<T> content; // 실제 데이터
    private PageMetaDto meta;   // 페이지 메타 데이터
}