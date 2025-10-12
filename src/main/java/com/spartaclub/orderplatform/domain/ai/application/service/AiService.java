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
        // 1. AI에 응답 생성 요청
        String generated = callExternalAi(prompt);

        // 2. 캐시 불러오기
        List<AiResponseDto> responses = aiCacheService.getCachedResponses(userId);

        // 3. 불러온 리스트에 응답 생성
        responses.add(AiResponseDto.builder()
                .prompt(prompt)
                .generatedText(generated)
                .isUsed(false)
                .build());

        // 4. 캐시에 임시 저장
        aiCacheService.updateCachedResponses(userId, responses);

        // 5. 상품 설명만 반환
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

    private String callExternalAi(String prompt) {
        // TODO: 실제 AI API 호출
        return "AI가 생성한 설명: " + prompt;
    }
}
