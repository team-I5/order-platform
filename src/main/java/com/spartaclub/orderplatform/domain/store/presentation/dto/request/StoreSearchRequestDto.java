package com.spartaclub.orderplatform.domain.store.presentation.dto.request;

import com.spartaclub.orderplatform.domain.store.domain.model.StoreStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StoreSearchRequestDto {

    @Schema(description = "가게 소유자 ID (Manager, Master 사용)", example = "123")
    private Long ownerId;

    @Schema(description = "가게 상태 (Manager, Master 사용)", example = "APPROVED")
    private StoreStatus status;
}
