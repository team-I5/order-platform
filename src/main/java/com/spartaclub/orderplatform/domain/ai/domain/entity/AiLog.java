package com.spartaclub.orderplatform.domain.ai.domain.entity;

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
@Builder
@AllArgsConstructor
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
    @Column(length = 20)
    private String status;
}

