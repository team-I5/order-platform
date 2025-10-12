package com.spartaclub.orderplatform.domain.ai.application.service;

import com.google.genai.types.Part;
import com.spartaclub.orderplatform.domain.ai.domain.entity.AiLog;
import com.spartaclub.orderplatform.domain.ai.infrastructure.repository.AiLogRepository;
import com.spartaclub.orderplatform.domain.ai.presentation.dto.AiResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static java.lang.Thread.sleep;

@Service
@Slf4j
public class AiService {

    private final AiCacheService aiCacheService;
    private final AiLogRepository aiLogRepository;
    private final Client geminiClient;

    public AiService(
            @Value("${google.gemini.api-key}") String apiKey,
            AiCacheService aiCacheService,
            AiLogRepository aiLogRepository
    ) {
        this.geminiClient = Client.builder()
                .apiKey(apiKey)
                .build();
        this.aiCacheService = aiCacheService;
        this.aiLogRepository = aiLogRepository;
    }


    /**
     * AI 응답 생성 후 캐시에 추가
     */
    public String generateAiResponse(String prompt, Long userId) {
        // AI 응답 생성
        String generated = callExternalAi(prompt);

        // 캐시에 저장
        List<AiResponseDto> responses = aiCacheService.getCachedResponses(userId);
        responses.add(AiResponseDto.builder()
                .prompt(prompt)
                .generatedText(generated)
                .isUsed(false)
                .build());
        aiCacheService.updateCachedResponses(userId, responses);

        return generated;
    }

    /**
     * Product 저장 시 캐시 → DB 이동
     * description과 캐시 마지막 응답 비교 후 used 판단
     */
    @Transactional
    public void saveAiLogsIfNeeded(Long userId, UUID productId, Long createdId, String description) {
        // 1. 캐시에 저장된 응답 조회
        List<AiResponseDto> responses = aiCacheService.getCachedResponses(userId);

        // 2. 저장된 데이터가 없으면 그대로 반환
        if (responses == null || responses.isEmpty()) return;

        // 3. 캐시 내의 모든 데이터 DB에 저장
        for (int i = 0; i < responses.size(); i++) {
            // 1. List의 마지막 데이터가 DB에 저장된 product의 설명과 동일한지 확인
            AiResponseDto response = responses.get(i);
            boolean isLast = (i == responses.size() - 1);
            response.setUsed(isLast && response.getGeneratedText().equals(description));

            // 2. 설명이 같으면 USED로, 다르면 NO_USE로 저장
            AiLog aiLog = AiLog.builder()
                    .productId(productId)
                    .prompt(response.getPrompt())
                    .generatedText(response.getGeneratedText())
                    .status(response.isUsed() ? "USED" : "NO_USE")
                    .createdId(createdId)
                    .build();
            aiLogRepository.save(aiLog);
        }

        // 4. 로그 저장 후 캐시 비움
        aiCacheService.evictCache(userId);
    }

    // gemini api 호출
    private String callExternalAi(String prompt) {
        try {
            String promptWithLimit = "3줄 이내로 답변해주세요.\n" + prompt;

            GenerateContentResponse response =
                    geminiClient.models.generateContent("gemini-2.5-flash-lite", promptWithLimit, null);

            return response.candidates().stream()
                    .findFirst()
                    .flatMap(c -> c.get(0).content())
                    .flatMap(content -> content.parts().stream()
                            .findFirst()
                            .flatMap(part -> part.stream().findFirst().flatMap(Part::text))
                    ).orElse("AI 응답 없음");
        } catch (Exception e) {
            log.warn(e.getMessage());
            return "AI 호출 중 오류 발생: " + e.getMessage();
        }

    }
}
