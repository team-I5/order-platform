package com.spartaclub.orderplatform.domain.ai.presentation.dto;

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
    private String prompt;
    private String generatedText;
    private boolean isUsed;
}
