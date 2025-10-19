package com.spartaclub.orderplatform.domain.ai.application.service;

import com.google.genai.types.Part;
import com.spartaclub.orderplatform.domain.ai.domain.entity.AiLog;
import com.spartaclub.orderplatform.domain.ai.domain.repository.AiLogRepository;
import com.spartaclub.orderplatform.domain.ai.infrastructure.repository.AiLogJPARepository;
import com.spartaclub.orderplatform.domain.ai.presentation.dto.AiResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

import java.util.List;
import java.util.UUID;

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
     * AI 로그 저장 (상품 생성/수정 공통)
     *
     * @param userId       사용자 ID
     * @param productId    상품 ID
     * @param description  현재 상품 설명
     * @param isUpdate     수정(update) 시 true, 생성(create) 시 false
     */
    @Transactional
    public void saveOrUpdateAiLogs(Long userId, UUID productId, String description, boolean isUpdate) {
        // 1. 수정 요청이라면 기존 USED 로그를 NO_USE로 변경
        if (isUpdate) {
            AiLog usedLogs = aiLogRepository.findByProductIdAndStatus(productId, "USED");
            usedLogs.setStatus("NO_USE");
        }

        // 2. 캐시에 저장된 응답 조회
        List<AiResponseDto> responses = aiCacheService.getCachedResponses(userId);
        if (responses == null || responses.isEmpty()) return;

        // 3. 캐시의 모든 응답을 DB에 저장
        for (int i = 0; i < responses.size(); i++) {
            AiResponseDto response = responses.get(i);
            boolean isLast = (i == responses.size() - 1);
            response.setUsed(isLast && response.getGeneratedText().equals(description));

            AiLog aiLog = AiLog.create(
                    productId,
                    response.getPrompt(),
                    response.getGeneratedText(),
                    response.isUsed() ? "USED" : "NO_USE"
            );

            aiLogRepository.save(aiLog);
        }

        // 4. 캐시 비우기
        aiCacheService.evictCache(userId);
    }

    // gemini api 호출
    private String callExternalAi(String prompt) {
        try {
            String promptWithLimit = "답변은 50글자 또는 3줄 이내로 답변해주세요.\n" + prompt;

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
