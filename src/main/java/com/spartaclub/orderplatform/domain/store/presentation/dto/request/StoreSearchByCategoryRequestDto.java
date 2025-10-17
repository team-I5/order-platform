package com.spartaclub.orderplatform.domain.store.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StoreSearchByCategoryRequestDto {

    @Schema(description = "음식점 카테고리", example = "KOREANFOOD")
    @NotBlank(message = "음식점 카테고리는 필수 입력사항입니다.")
    private String categoryType;
}
