package com.spartaclub.orderplatform.domain.ai.application.infrastructure.repository;

import com.spartaclub.orderplatform.domain.ai.domain.entity.AiLog;
import com.spartaclub.orderplatform.domain.ai.infrastructure.repository.AiLogJPARepository;
import com.spartaclub.orderplatform.domain.ai.infrastructure.repository.AiLogRepositoryImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@DisplayName("AiLogRepositoryImpl 단위 테스트")
class AiLogRepositoryImplTest {

    @Mock
    private AiLogJPARepository aiLogJPARepository;

    @InjectMocks
    private AiLogRepositoryImpl aiLogRepository;

    @Nested
    @DisplayName("save() 테스트")
    class SaveTest {

        @Test
        @DisplayName("AI 로그를 정상적으로 저장한다")
        void save_success() {
            // given
            AiLog aiLog = mock(AiLog.class);
            given(aiLogJPARepository.save(aiLog)).willReturn(aiLog);

            // when
            AiLog result = aiLogRepository.save(aiLog);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(aiLog);
            then(aiLogJPARepository).should().save(aiLog);
        }
    }

    @Nested
    @DisplayName("findByProductIdAndStatus() 테스트")
    class FindByProductIdAndStatusTest {

        @Test
        @DisplayName("상품 ID와 상태로 로그를 조회한다")
        void findByProductIdAndStatus_success() {
            // given
            UUID productId = UUID.randomUUID();
            String status = "USED";
            AiLog aiLog = mock(AiLog.class);

            given(aiLogJPARepository.findByProductIdAndStatus(productId, status)).willReturn(aiLog);

            // when
            AiLog result = aiLogRepository.findByProductIdAndStatus(productId, status);

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(aiLog);
            then(aiLogJPARepository).should().findByProductIdAndStatus(productId, status);
        }
    }
}
