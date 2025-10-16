package com.spartaclub.orderplatform.domain.ai.presentation.controller;

import com.spartaclub.orderplatform.domain.ai.application.service.AiService;
import com.spartaclub.orderplatform.global.presentation.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.spartaclub.orderplatform.global.application.security.SecurityUtils.getCurrentUserId;

@RestController
@RequestMapping("/v1/products/ai")
@RequiredArgsConstructor
@Tag(name = "AI API", description = "AI 상품 설명 생성 관련 API")
public class AiController {

    private final AiService aiService;

    @Operation(
            summary = "상품 설명 AI 생성",
            description = """
                    입력한 프롬프트를 기반으로 상품 설명을 AI가 생성합니다.<br><br>
                    prompt: 상품 정보를 설명하는 키워드 또는 문장<br>
                    예시) '마라탕, 매콤, 인기' -> AI가 50자 내외로 상품 설명 생성
                    """
    )
    @PostMapping("/description")
    public ResponseEntity<ApiResponse<String>> generateDescription(
            @Parameter(description = "AI에게 전달할 프롬프트", example = "마라탕, 매콤, 인기")
            @RequestParam String prompt
    ) {
        Long userId = getCurrentUserId();
        String generatedText = aiService.generateAiResponse(prompt, userId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(generatedText));
    }
}
