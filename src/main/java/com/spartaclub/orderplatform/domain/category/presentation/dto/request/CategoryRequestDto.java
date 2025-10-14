package com.spartaclub.orderplatform.domain.category.presentation.dto.request;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
 * 카테고리 요청 Dto
 *
 * @author
 * @date 2025-10-14
 */
@Getter
@NoArgsConstructor
public class CategoryRequestDto {

    @Column(nullable = false)
    private String name;
}
