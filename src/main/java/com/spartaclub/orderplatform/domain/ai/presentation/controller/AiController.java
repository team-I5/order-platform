package com.spartaclub.orderplatform.domain.ai.presentation.controller;

import com.spartaclub.orderplatform.domain.ai.application.service.AiService;
import com.spartaclub.orderplatform.global.presentation.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/products/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/description")
    public ResponseEntity<ApiResponse<String>> generateDescription(
            @RequestParam String prompt
    ) {
        String generateDescription = aiService.generateAiResponse(prompt, 0L);
        return ResponseEntity.ok(ApiResponse.success(generateDescription)); // 캐시 key 반환
    }
}
