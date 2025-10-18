package com.spartaclub.orderplatform.domain.product.infrastructure.repository;

import com.spartaclub.orderplatform.domain.review.domain.model.Review;
import com.spartaclub.orderplatform.domain.review.infrastructure.repository.ReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductReviewReaderRepositoryImpl 단위 테스트")
class ProductReviewReaderRepositoryImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ProductReviewReaderRepositoryImpl productReviewReaderRepository;

    @Nested
    @DisplayName("findAllByProduct_ProductId() 테스트")
    class FindAllByProductIdTest {

        @Test
        @DisplayName("상품 ID로 리뷰 목록 조회 성공")
        void findAllByProductId_success() {
            // given
            UUID productId = UUID.randomUUID();
            Pageable pageable = Pageable.unpaged();
            Review review1 = mock(Review.class);
            Review review2 = mock(Review.class);
            Page<Review> reviewPage = new PageImpl<>(List.of(review1, review2));

            given(reviewRepository.findAllByProduct_ProductId(productId, pageable))
                    .willReturn(reviewPage);

            // when
            Page<Review> result = productReviewReaderRepository.findAllByProduct_ProductId(productId, pageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(2);
            then(reviewRepository).should().findAllByProduct_ProductId(productId, pageable);
        }
    }
}
