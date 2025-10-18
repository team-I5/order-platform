package com.spartaclub.orderplatform.domain.category.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
 * 카테고리 요청 Dto
 *
 * @author
 * @date 2025-10-14
 */
@Schema(description = "카테고리 요청 정보")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequestDto {

    @Schema(description = "카테고리 키워드", example = "한식")
    @NotBlank(message = "카테고리 키워드는 필수 입력사항입니다.")
    private String name;

    public static CategoryRequestDto of(String name) {
        return new CategoryRequestDto(name);
    }
}
