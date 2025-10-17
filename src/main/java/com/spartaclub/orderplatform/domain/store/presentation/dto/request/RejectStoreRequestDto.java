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
public class RejectStoreRequestDto {

    @NotBlank(message = "음식점 승인 거절 사유는 필수 입력 사항입니다.")
    @Size(max = 250, message = "승인 거절 사유는 250자 이내로 작성해주세요.")
    @Schema(description = "거절 사유", example = "부적절한 음식점 설명입니다.")
    private String rejectReason;
}
