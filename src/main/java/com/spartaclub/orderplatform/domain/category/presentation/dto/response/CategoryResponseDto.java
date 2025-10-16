package com.spartaclub.orderplatform.domain.category.presentation.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

/*
 * 카테고리 응답 Dto
 *
 * @author 이준성
 * @date 2025-10-14
 */
@Getter
@AllArgsConstructor
public class CategoryResponseDto {

    // 카테고리 ID
    private UUID categoryId;
    // 카테고리명
    private String type;
}
