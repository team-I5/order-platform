package com.spartaclub.orderplatform.domain.review.infrastructure.repository;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.spartaclub.orderplatform.domain.order.domain.model.Order;
import com.spartaclub.orderplatform.domain.product.domain.entity.Product;
import com.spartaclub.orderplatform.domain.review.domain.model.Review;
import com.spartaclub.orderplatform.domain.store.domain.model.Store;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class ReviewRepositoryTest {

    @Mock
    ReviewRepository reviewRepository;

    Pageable pageable = PageRequest.of(0, 10);
    User user = mock(User.class);
    Store store = mock(Store.class);
    Order order = mock(Order.class);
    Product product = mock(Product.class);

    @Test
    @DisplayName("existsByOrderIdAndDeletedAtISNull")
    void existsByOrderIdAndDeletedAtISNull() {
        UUID orderId = UUID.randomUUID();
        given(reviewRepository.existsByOrder_OrderIdAndDeletedAtIsNull(orderId)).willReturn(true);
        boolean rlt = reviewRepository.existsByOrder_OrderIdAndDeletedAtIsNull(orderId);
        Assertions.assertTrue(rlt);
        then(reviewRepository).should(times(1))
            .existsByOrder_OrderIdAndDeletedAtIsNull(orderId);
    }

    @Test
    @DisplayName("findByUserAndDeletedAtIsNull")
    void findByUserAndDeletedAtIsNull() {
        Page<Review> dummyPage = new PageImpl<>(List.of(mock(Review.class)));
        given(reviewRepository.findByUser_UserIdAndDeletedAtIsNull(user.getUserId(), pageable))
            .willReturn(dummyPage);
        Page<Review> rlt = reviewRepository.findByUser_UserIdAndDeletedAtIsNull(user.getUserId(),
            pageable);
        Assertions.assertEquals(rlt, dummyPage);
        then(reviewRepository).should(times(1))
            .findByUser_UserIdAndDeletedAtIsNull(user.getUserId(), pageable);
    }

    @Test
    @DisplayName("findByStoreNameAndDeletedAtIsNull")
    void findByStoreNameAndDeletedAtIsNull() {
        String storeName = store.getStoreName();
        Page<Review> dummyPage = new PageImpl<>(List.of(mock(Review.class)));
        given(reviewRepository.findByStore_StoreNameAndDeletedAtIsNull(storeName, pageable))
            .willReturn(dummyPage);
        Page<Review> rlt = reviewRepository.findByStore_StoreNameAndDeletedAtIsNull(storeName,
            pageable);
        Assertions.assertEquals(rlt, dummyPage);
        then(reviewRepository).should(times(1))
            .findByStore_StoreNameAndDeletedAtIsNull(storeName, pageable);
    }

    @Test
    @DisplayName("findByUserIdAndOrderIdAndDeletedAtIsNull")
    void findByUserIdAndOrderIdAndDeletedAtIsNull() {
        UUID orderId = order.getOrderId();
        Long userId = user.getUserId();
        Page<Review> dummyPage = new PageImpl<>(List.of(mock(Review.class)));
        given(reviewRepository.findByUser_UserIdAndOrder_OrderIdAndDeletedAtIsNull(userId, orderId,
            pageable)).willReturn(dummyPage);
        Page<Review> rlt = reviewRepository.findByUser_UserIdAndOrder_OrderIdAndDeletedAtIsNull(
            userId, orderId, pageable);
        Assertions.assertEquals(rlt, dummyPage);
        then(reviewRepository).should(times(1))
            .findByUser_UserIdAndOrder_OrderIdAndDeletedAtIsNull(userId, orderId, pageable);
    }

    @Test
    @DisplayName("findByRatingAndDeletedAtIsNull")
    void findByRatingAndDeletedAtIsNull() {
        Integer rating = 4;
        Page<Review> dummyPage = new PageImpl<>(List.of(mock(Review.class)));
        given(reviewRepository.findByRatingAndDeletedAtIsNull(rating, pageable))
            .willReturn(dummyPage);
        Page<Review> rlt = reviewRepository.findByRatingAndDeletedAtIsNull(rating, pageable);
        Assertions.assertEquals(rlt, dummyPage);
        then(reviewRepository).should(times(1))
            .findByRatingAndDeletedAtIsNull(rating, pageable);
    }

    @Test
    @DisplayName("findByContentsContainingAndDeletedAtIsNull")
    void findByContentsContainingAndDeletedAtIsNull() {
        String keyword = "배달";
        Page<Review> dummyPage = new PageImpl<>(List.of(mock(Review.class)));
        given(reviewRepository.findByContentsContainingAndDeletedAtIsNull(keyword, pageable))
            .willReturn(dummyPage);
        Page<Review> rlt = reviewRepository.findByContentsContainingAndDeletedAtIsNull(keyword,
            pageable);
        Assertions.assertEquals(rlt, dummyPage);
        then(reviewRepository).should(times(1))
            .findByContentsContainingAndDeletedAtIsNull(keyword, pageable);
    }

    @Test
    @DisplayName("findByDeletedAtIsNull")
    void findByDeletedAtIsNull() {
        Page<Review> dummyPage = new PageImpl<>(List.of(mock(Review.class)));
        given(reviewRepository.findByDeletedAtIsNull(pageable))
            .willReturn(dummyPage);
        Page<Review> rlt = reviewRepository.findByDeletedAtIsNull(pageable);
        Assertions.assertEquals(rlt, dummyPage);
        then(reviewRepository).should(times(1))
            .findByDeletedAtIsNull(pageable);
    }

    @Test
    @DisplayName("findAllByProductId")
    void findAllByProductId() {
        UUID productId = product.getProductId();
        Page<Review> dummyPage = new PageImpl<>(List.of(mock(Review.class)));
        given(reviewRepository.findAllByProduct_ProductId(productId, pageable))
            .willReturn(dummyPage);
        Page<Review> rlt = reviewRepository.findAllByProduct_ProductId(productId, pageable);
        Assertions.assertEquals(rlt, dummyPage);
        then(reviewRepository).should(times(1))
            .findAllByProduct_ProductId(productId, pageable);
    }

    @Test
    @DisplayName("findReviewCountAndAverageForAllStores()")
    void findReviewCountAndAverageForAllStores() {
        UUID storeId1 = UUID.randomUUID();
        UUID storeId2 = UUID.randomUUID();
        Object[] rlt1 = new Object[]{storeId1, 4.2, 3L};
        Object[] rlt2 = new Object[]{storeId2, 3.4, 5L};

        List<Object[]> dummyList = List.of(rlt1, rlt2);
        given(reviewRepository.findReviewCountAndAverageForAllStores())
            .willReturn(dummyList);
        List<Object[]> rlt = reviewRepository.findReviewCountAndAverageForAllStores();
        Assertions.assertEquals(rlt, dummyList);
        then(reviewRepository).should(times(1))
            .findReviewCountAndAverageForAllStores();
    }
}
