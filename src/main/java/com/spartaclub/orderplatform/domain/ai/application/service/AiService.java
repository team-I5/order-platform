package com.spartaclub.orderplatform.domain.ai.application.service;

import com.spartaclub.orderplatform.domain.ai.domain.entity.AiLog;
import com.spartaclub.orderplatform.domain.ai.infrastructure.repository.AiLogRepository;
import com.spartaclub.orderplatform.domain.ai.presentation.dto.AiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AiService {

    private final AiCacheService aiCacheService;
    private final AiLogRepository aiLogRepository;

    /**
     * AI 응답 생성 후 캐시에 추가
     */
    public String generateAiResponse(String prompt, Long userId) {
        String generated = callExternalAi(prompt);

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
        List<AiResponseDto> responses = aiCacheService.getCachedResponses(userId);
        if (responses == null || responses.isEmpty()) return;

        // 마지막 응답이 실제 상품 설명과 같은지 확인
        for (int i = 0; i < responses.size(); i++) {
            AiResponseDto response = responses.get(i);
            boolean isLast = (i == responses.size() - 1);
            response.setUsed(isLast && response.getGeneratedText().equals(description));

            AiLog aiLog = AiLog.builder()
                    .productId(productId)
                    .prompt(response.getPrompt())
                    .generatedText(response.getGeneratedText())
                    .status(response.isUsed() ? "USED" : "NO_USE")
                    .createdId(createdId)
                    .build();
            aiLogRepository.save(aiLog);
        }

        // 로그 저장 후 캐시 비움
        aiCacheService.evictCache(userId);
    }

    private String callExternalAi(String prompt) {
        // TODO: 실제 AI API 호출
        return "AI가 생성한 설명: " + prompt;
    }
}
