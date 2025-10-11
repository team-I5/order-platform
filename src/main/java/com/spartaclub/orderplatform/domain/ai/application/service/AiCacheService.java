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

    @Cacheable(value = "aiResponses", key = "#userId")
    public List<AiResponseDto> getCachedResponses(Long userId) {
        return new ArrayList<>(); // 캐시에 없으면 빈 리스트 반환
    }

    @CachePut(value = "aiResponses", key = "#userId")
    public List<AiResponseDto> updateCachedResponses(Long userId, List<AiResponseDto> responses) {
        return responses;
    }

    @CacheEvict(value = "aiResponses", key = "#userId")
    public void evictCache(Long userId) {}
}
