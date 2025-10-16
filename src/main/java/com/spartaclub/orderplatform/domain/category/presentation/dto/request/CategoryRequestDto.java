package com.spartaclub.orderplatform.domain.category.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank
    private String name;
}
