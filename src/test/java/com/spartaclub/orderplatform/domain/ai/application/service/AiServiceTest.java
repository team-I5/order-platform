package com.spartaclub.orderplatform.domain.ai.application.service;

import com.google.genai.Client;
import com.spartaclub.orderplatform.domain.ai.domain.entity.AiLog;
import com.spartaclub.orderplatform.domain.ai.domain.repository.AiLogRepository;
import com.spartaclub.orderplatform.domain.ai.presentation.dto.AiResponseDto;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AiService 단위 테스트 (AI 호출 없이 캐시/로그 검증)")
class AiServiceTest {

    @Mock
    private AiCacheService aiCacheService;

    @Mock
    private AiLogRepository aiLogRepository;

    private AiService aiService;

    private final Long userId = 1L;
    private UUID productId;
    private AiResponseDto dto1;
    private AiResponseDto dto2;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();

        dto1 = AiResponseDto.builder()
                .prompt("prompt1")
                .generatedText("text1")
                .isUsed(false)
                .build();

        dto2 = AiResponseDto.builder()
                .prompt("prompt2")
                .generatedText("text2")
                .isUsed(false)
                .build();

        aiService = new AiService("dummy", aiCacheService, aiLogRepository);
    }

    // --------------------------------------------------------------------
    @Nested
    @DisplayName("saveOrUpdateAiLogs() - AI 로그 저장/수정 로직")
    class SaveOrUpdateAiLogsTest {

        @Test
        @DisplayName("isUpdate=false → 캐시의 모든 응답이 DB에 저장된다.")
        void success_saveAiLogs_create() {
            // given
            given(aiCacheService.getCachedResponses(userId))
                    .willReturn(List.of(dto1, dto2));

            // when
            aiService.saveOrUpdateAiLogs(userId, productId, "text2", false);

            // then
            then(aiLogRepository).should(times(2)).save(any(AiLog.class));
            then(aiCacheService).should(times(1)).evictCache(userId);
        }

        @Test
        @DisplayName("isUpdate=true → 기존 USED 로그를 NO_USE로 바꾸고, 캐시를 저장한다.")
        void success_saveAiLogs_update() {
            // given
            AiLog usedLog = AiLog.create(productId, "prompt-old", "text-old", "USED");
            given(aiLogRepository.findByProductIdAndStatus(productId, "USED"))
                    .willReturn(usedLog);

            given(aiCacheService.getCachedResponses(userId))
                    .willReturn(List.of(dto1));

            // when
            aiService.saveOrUpdateAiLogs(userId, productId, "text1", true);

            // then
            assertThat(usedLog.getStatus()).isEqualTo("NO_USE");
            then(aiLogRepository).should(times(1)).save(any(AiLog.class));
            then(aiCacheService).should(times(1)).evictCache(userId);
        }

        @Test
        @DisplayName("캐시에 아무 데이터가 없으면 아무 작업도 수행하지 않는다.")
        void success_noCacheData() {
            // given
            given(aiCacheService.getCachedResponses(userId)).willReturn(Collections.emptyList());

            // when
            aiService.saveOrUpdateAiLogs(userId, productId, "text", false);

            // then
            then(aiLogRepository).should(never()).save(any(AiLog.class));
            then(aiCacheService).should(never()).evictCache(userId);
        }
    }
}
