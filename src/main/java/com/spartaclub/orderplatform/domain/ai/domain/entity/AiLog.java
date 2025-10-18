package com.spartaclub.orderplatform.domain.ai.domain.entity;

import com.spartaclub.orderplatform.domain.product.domain.entity.Product;
import com.spartaclub.orderplatform.domain.store.domain.model.Store;
import com.spartaclub.orderplatform.global.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;

import java.util.UUID;

/**
 * AI 요청 기록 Entity
 *
 * @author 류형선
 * @date 2025-10-09(목)
 */
@Entity
@Table(name = "p_ai_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AiLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID aiLogId;

    // Product 연결 (null 허용)
    private UUID productId;

    // 요청 프롬프트
    @Column(nullable = false, length = 500)
    private String prompt;

    // AI 응답
    @Column(length = 1000)
    private String generatedText;

    // 결과 사용 여부 (e.g. "USED", "UNUSED")
    @Setter
    @Column(length = 20)
    private String status;

    // 정적 팩토리 메소드
    public static AiLog create(UUID productId, String prompt, String generatedText, String status) {
        AiLog aiLog = new AiLog();
        aiLog.productId = productId;
        aiLog.prompt = prompt;
        aiLog.generatedText = generatedText;
        aiLog.status = status;
        return aiLog;
    }
}

