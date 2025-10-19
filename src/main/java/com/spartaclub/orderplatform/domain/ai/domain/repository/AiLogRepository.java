package com.spartaclub.orderplatform.domain.ai.domain.repository;

import com.spartaclub.orderplatform.domain.ai.domain.entity.AiLog;

import java.util.List;
import java.util.UUID;

public interface AiLogRepository {

    AiLog save(AiLog aiLog);

    AiLog findByProductIdAndStatus(UUID productId, String used);
}
