package com.spartaclub.orderplatform.domain.ai.infrastructure.repository;

import com.spartaclub.orderplatform.domain.ai.domain.entity.AiLog;
import com.spartaclub.orderplatform.domain.ai.domain.repository.AiLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AiLogRepositoryImpl implements AiLogRepository {

    private final AiLogJPARepository aiLogJPARepository;

    @Override
    public AiLog save(AiLog aiLog) {
        return aiLogJPARepository.save(aiLog);
    }

    @Override
    public AiLog findByProductIdAndStatus(UUID productId, String used) {
        return aiLogJPARepository.findByProductIdAndStatus(productId, used);
    }
}
