package com.spartaclub.orderplatform.domain.ai.infrastructure.repository;

import com.spartaclub.orderplatform.domain.ai.domain.entity.AiLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AiLogRepository extends JpaRepository<AiLog, UUID> {
}
