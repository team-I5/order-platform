package com.spartaclub.orderplatform.domain.ai.presentation.controller;

import com.spartaclub.orderplatform.domain.ai.application.service.AiService;
import com.spartaclub.orderplatform.global.presentation.dto.ApiResponse;
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
public class AiController {

    private final AiService aiService;

    @PostMapping("/description")
    public ResponseEntity<ApiResponse<String>> generateDescription(
            @RequestParam String prompt
    ) {
        Long userId = getCurrentUserId();
        String generatedText = aiService.generateAiResponse(prompt, userId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(generatedText));
    }
}
