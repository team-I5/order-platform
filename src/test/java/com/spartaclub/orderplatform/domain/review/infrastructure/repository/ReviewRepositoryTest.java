package com.spartaclub.orderplatform.domain.review.infrastructure.repository;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ReviewRepositoryTest {

    @Mock
    ReviewRepository reviewRepository;

    @Test
    @DisplayName("existsByOrderIdAndDeletedAtISNull")
    void existsByOrderIdAndDeletedAtISNull() {
        UUID orderId = UUID.randomUUID();
        given(reviewRepository.existsByOrder_OrderIdAndDeletedAtIsNull(orderId)).willReturn(true);
        boolean rlt = reviewRepository.existsByOrder_OrderIdAndDeletedAtIsNull(orderId);
        Assertions.assertTrue(rlt);
        then(reviewRepository).should(times(1)).
            existsByOrder_OrderIdAndDeletedAtIsNull(orderId);
    }

}
