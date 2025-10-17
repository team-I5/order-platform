package com.spartaclub.orderplatform.domain.store.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StoreSearchByKeywordRequestDto {

    @Schema(description = "음식점 키워드", example = "피자")
    @Size(min = 1, max = 10, message = "키워드는 1자 이상 10자 이내로 작성해주세요.")
    @NotBlank(message = "음식점 키워드는 필수 입력사항입니다.")
    private String storeName;
}
