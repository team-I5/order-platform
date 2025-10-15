package com.spartaclub.orderplatform.domain.ai.application.service;

import com.spartaclub.orderplatform.domain.ai.presentation.dto.AiResponseDto;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AiCacheService {

    // 캐시에 저장된 데이터 조회
    @Cacheable(value = "aiResponses", key = "#userId")
    public List<AiResponseDto> getCachedResponses(Long userId) {
        return new ArrayList<>(); // 캐시에 없으면 빈 리스트 반환
    }

    // 캐시 데이터 업데이트
    @CachePut(value = "aiResponses", key = "#userId")
    public List<AiResponseDto> updateCachedResponses(Long userId, List<AiResponseDto> responses) {
        return responses;
    }

    // 캐시 비우기
    @CacheEvict(value = "aiResponses", key = "#userId")
    public void evictCache(Long userId) {}
}
