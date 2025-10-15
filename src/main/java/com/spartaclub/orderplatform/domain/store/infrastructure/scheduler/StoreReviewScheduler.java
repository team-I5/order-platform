package com.spartaclub.orderplatform.domain.store.infrastructure.scheduler;

import com.spartaclub.orderplatform.domain.review.repository.ReviewRepository;
import com.spartaclub.orderplatform.domain.store.domain.model.Store;
import com.spartaclub.orderplatform.domain.store.domain.repository.StoreRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StoreReviewScheduler {

    private final StoreRepository storeRepository;
    private final ReviewRepository reviewRepository;

    @Scheduled(cron = "0 0 0/3 * * *")
    @Transactional
    public void updateStoreAverageRatingAndReviewCount() {

        List<Object[]> reviews = reviewRepository.findReviewCountAndAverageForAllStores();

        if (reviews.isEmpty()) {
            return;
        }

        List<UUID> storeIds = reviews.stream().map(r -> (UUID) r[0]).toList();

        Map<UUID, Store> storeMap = storeRepository.findAllById(storeIds);

        for (Object[] review : reviews) {
            UUID storeId = (UUID) review[0];
            Double averageRating = (Double) review[1];
            Long reviewCountLong = (Long) review[2];
            int reviewCount = reviewCountLong.intValue();

            Store store = storeMap.get(storeId);
            if (store != null) {
                store.updateAverageRatingAndReviewCount(averageRating, reviewCount);
            }
        }
    }
}