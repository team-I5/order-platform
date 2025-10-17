package com.spartaclub.orderplatform.domain.store.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StoreCategoryRequestDto {

    @Schema(description = "카테고리 ID 리스트", example = "[\"3fa85f64-5717-4562-b3fc-2c963f66afa6\"]")
    private List<UUID> categoryIds;
}
