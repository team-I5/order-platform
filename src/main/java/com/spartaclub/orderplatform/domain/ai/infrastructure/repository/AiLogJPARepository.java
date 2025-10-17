package com.spartaclub.orderplatform.domain.ai.infrastructure.repository;

import com.spartaclub.orderplatform.domain.ai.domain.entity.AiLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AiLogJPARepository extends JpaRepository<AiLog, UUID> {
    List<AiLog> findAllByProductIdAndStatus(UUID productId, String used);

    AiLog findByProductIdAndStatus(UUID productId, String used);
}
