package com.spartaclub.orderplatform.domain.ai.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 상품 등록 응답 Dto
 *
 * @author 류형선
 * @date 2025-10-01(수)
 */
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AiResponseDto {

    @NotBlank(message = "Prompt는 필수입니다.")
    private String prompt;

    @NotBlank(message = "GeneratedText는 필수입니다.")
    @Size(max = 500, message = "GeneratedText는 최대 500자까지 허용됩니다.")
    private String generatedText;

    @Builder.Default
    private boolean isUsed = false;
}
