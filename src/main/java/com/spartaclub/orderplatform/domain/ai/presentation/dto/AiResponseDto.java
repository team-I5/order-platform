package com.spartaclub.orderplatform.domain.ai.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 상품 등록 응답 Dto
 */
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI 응답 DTO")
public class AiResponseDto {

    @NotBlank(message = "Prompt는 필수입니다.")
    @Schema(description = "AI에 전달한 프롬프트", example = "마라탕, 매콤, 인기")
    private String prompt;

    @NotBlank(message = "GeneratedText는 필수입니다.")
    @Size(max = 500, message = "GeneratedText는 최대 500자까지 허용됩니다.")
    @Schema(description = "AI가 생성한 텍스트", example = "이 마라탕은 매콤하고 진한 국물 맛이 일품입니다.")
    private String generatedText;

    @Builder.Default
    @Schema(description = "이미 사용했는지 여부", example = "false")
    private boolean isUsed = false;
}
